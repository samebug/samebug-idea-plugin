/**
 * Copyright 2017 Samebug, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.samebug.clients.idea.search.console;

import com.intellij.execution.impl.ConsoleViewImpl;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.event.DocumentAdapter;
import com.intellij.openapi.editor.event.DocumentEvent;
import com.intellij.openapi.editor.markup.HighlighterLayer;
import com.intellij.openapi.editor.markup.MarkupModel;
import com.intellij.openapi.editor.markup.RangeHighlighter;
import com.intellij.openapi.project.Project;
import com.intellij.util.containers.HashMap;
import com.intellij.util.messages.MessageBusConnection;
import com.samebug.clients.common.entities.search.*;
import com.samebug.clients.common.services.SearchRequestStore;
import com.samebug.clients.idea.components.application.IdeaSamebugPlugin;
import com.samebug.clients.idea.search.SearchRequestListener;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/*
    Handling the gutter icon on the console view.

    As there are multiple threads involved in this story, take extreme care when you modify something.
    The most important thing is to make sure that if you are on the EDT, that code must be able to execute fast (under 100 millis).
    This means e.g. that you can't search for the stacktraces in the document text in the EDT. On the other hand, when you create the markers on the gutter on the EDT,
    you cannot assume that the content of the document is still the same.

    There are a few assumptions safe to make:
     - documentChanged is called on the Event Dispatcher Thread
     - the rest of the public methods are called on background threads
     - when newSearchRequest is called, the SearchRequestService is already called, so this request was inserted to the SearchRequestStore already. However, it does not mean that
     this request is PRESENT in the SearchRequestStore, as it can be already deleted by an other thread.
     - the same is true for the rest of the search related methods (first the service is called, than this ConsoleWatcher
     - documentChange is called relatively rare. When a lot of things are written to the console, it will not trigger for every character or every line,
      but only for a few times for chunck of new text.

    There are a few assumptions you would think to be safe, but are actually not true:
     - the content of the document is not append-only. It has a buffer with limited size, and the first characters are getting removed when the limit is reached (rotating).
     - the content of the console is updated on the EDT, but the searches are parsed in a background thread from an other stream. It means that when newSearchRequest is triggered
     with a stacktrace, that stacktrace might or might not be present on the console
     - nothing guarantees that newSearchRequest will be triggered before savedSearch for the same request.
      Under higher load (50+ stacktrace) it happens to be out of order quite frequently.


     TODO: it has bugs because it assumes the order of search-related calls.
 */
public class ConsoleWatcher extends DocumentAdapter implements SearchRequestListener {
    private final Logger LOGGER = Logger.getInstance(ConsoleWatcher.class);

    private final DebugSessionInfo sessionInfo;
    private final Editor editor;
    private final SearchRequestStore searchRequestStore;
    private final Map<UUID, RangeHighlighter> highlights;

    public ConsoleWatcher(Project project, ConsoleViewImpl console, DebugSessionInfo sessionInfo) {
        this.sessionInfo = sessionInfo;
        this.editor = console.getEditor();
        this.searchRequestStore = IdeaSamebugPlugin.getInstance().searchRequestStore;
        this.highlights = new ConcurrentHashMap<UUID, RangeHighlighter>();
        LOGGER.info("Watcher constructed for " + editor.toString());

        editor.getDocument().addDocumentListener(this, console);
        MessageBusConnection messageBusConnection = project.getMessageBus().connect(console);
        messageBusConnection.subscribe(SearchRequestListener.TOPIC, this);
    }

    @Override
    public void documentChanged(DocumentEvent e) {
        ApplicationManager.getApplication().executeOnPooledThread(new Runnable() {
            @Override
            public void run() {
                rebuildMarkers();
            }
        });
    }

    @Override
    public void newSearchRequest(final RequestedSearch requestedSearch) {
        if (!sessionInfo.equals(requestedSearch.getSearchInfo().sessionInfo)) return;
        final UUID requestId = requestedSearch.getSearchInfo().requestId;
        final Document document = editor.getDocument();
        final StringBuilder consoleContent = new StringBuilder(document.getText());
        final int traceOffset = findStacktraceOffset(consoleContent, requestedSearch.getTrace());
        ApplicationManager.getApplication().invokeLater(new Runnable() {
            @Override
            public void run() {
                RangeHighlighter newHighlighter;
                // NOTE it happens that this method gets called for the same requestId multiple times. Make sure we don't create multiple marker for the same request
                if (highlights.get(requestId) == null) {
                    newHighlighter = addMarkerAtOffset(traceOffset, createMarker(requestedSearch));
                    if (newHighlighter != null) {
                        // new trace is found in the console, add the marker
                        highlights.put(requestId, newHighlighter);
                    } else {
                        // this trace is not found, so we could remove it from request store
                        // However in practice a frequent case is that the document is not yet updated with the new content.
                        // Not deleting won't hurt anything, as a documentUpdate will rebuild every marker, and remove the request if the trace is still not found.
                    }
                }
            }
        });
    }

    @Override
    public void savedSearch(final SavedSearch savedSearch) {
        if (!sessionInfo.equals(savedSearch.getSearchInfo().sessionInfo)) return;
        final UUID requestId = savedSearch.getSearchInfo().requestId;
        final Document document = editor.getDocument();
        final RangeHighlighter highlightForRequest = highlights.get(requestId);

        if (highlightForRequest == null) {
            // not sure if this can happen, but after rebuilding we surely have a clean and consistent state
            rebuildMarkers();
        } else {
            ApplicationManager.getApplication().invokeLater(new Runnable() {
                @Override
                public void run() {
                    RangeHighlighter newHighlighter;
                    // NOTE actualHighlightForRequest might be different from highlightForRequest, as we are on a different thread since the previous get.
                    RangeHighlighter actualHighlightForRequest = highlights.get(requestId);
                    if (actualHighlightForRequest != null) {
                        int originalStartOffset = actualHighlightForRequest.getStartOffset();
                        // If there was a highlight previously, remove it from the gutter.
                        actualHighlightForRequest.dispose();

                        // if there is a marker for the requested search, we might have to correct it,
                        // i.e. move a few lines down if it turns out that the stacktrace starts not exactly in that line.
                        if (0 <= originalStartOffset && originalStartOffset < document.getTextLength()) {
                            int originalStartLine = document.getLineNumber(originalStartOffset);
                            Integer correction = savedSearch.getSavedSearch().getFirstLine();
                            int correctedStartLine = originalStartLine;
                            if (correction != null) correctedStartLine += correction;
                            int correctedOffset = document.getLineStartOffset(correctedStartLine);
                            newHighlighter = addMarkerAtOffset(correctedOffset, createMarker(savedSearch));
                        } else {
                            newHighlighter = null;
                        }
                    } else {
                        newHighlighter = null;
                    }

                    if (newHighlighter != null) {
                        highlights.put(requestId, newHighlighter);
                    } else {
                        highlights.remove(requestId);
                        // if the stacktrace is not found in the console, we have to notify the searchRequestStore that this request is no longer interesting
                        searchRequestStore.removeRequest(requestId);
                    }
                }
            });
        }
    }

    @Override
    public void failedSearch(final SearchInfo searchInfo) {
        if (!sessionInfo.equals(searchInfo.sessionInfo)) return;
        final UUID requestId = searchInfo.requestId;
        LOGGER.info("Failed search from editor " + editor.toString());

        final RangeHighlighter highlightForRequest = highlights.get(requestId);
        if (highlightForRequest != null) {
            ApplicationManager.getApplication().invokeLater(new Runnable() {
                @Override
                public void run() {
                    highlightForRequest.dispose();
                    highlights.remove(requestId);
                    // We do not have to remove it from searchRequestStore, in this case the controller noticed it and is already removed

                    // If this was the last mark, hide the gutter area
                    if (highlights.isEmpty()) editor.getSettings().setLineMarkerAreaShown(false);
                }
            });
        }
    }

    private void rebuildMarkers() {
        Document document = editor.getDocument();
        final Map<UUID, SearchRequest> currentRequests = searchRequestStore.getRequests(sessionInfo);
        final Map<UUID, Integer> requestOffsets = findStacktraceOffsets(document.getText(), currentRequests);

        ApplicationManager.getApplication().invokeLater(new Runnable() {
            @Override
            public void run() {
                for (RangeHighlighter h : highlights.values()) {
                    h.dispose();
                }
                highlights.clear();
                editor.getSettings().setLineMarkerAreaShown(false);

                for (Map.Entry<UUID, Integer> requestOffsetEntry : requestOffsets.entrySet()) {
                    final UUID requestId = requestOffsetEntry.getKey();
                    final SearchRequest request = currentRequests.get(requestId);
                    final RangeHighlighter highlight;
                    final int offset = requestOffsetEntry.getValue();
                    assert request != null : "currentRequests and requestOffsets should contain the same keys";

                    final SearchMark mark = createMarker(request);
                    if (mark != null) highlight = addMarkerAtOffset(offset, mark);
                    else highlight = null;

                    if (highlight != null) {
                        // We found the stacktrace in the content of the console
                        highlights.put(requestId, highlight);
                    } else {
                        // We have not found the stacktrace in the content of the console.
                        // Probably an other process wrote something in between the lines of the trace, or it was rolled over when exceeding console buffer capacity.
                        //searchRequestStore.removeRequest(requestId);
                        // UPDATE console content can change due to filters.
                        // If we remove the search just because if was not found once, we will fail to find it later, when the filter is turned off.
                    }
                }
            }
        });
    }

    private Map<UUID, Integer> findStacktraceOffsets(String document, Map<UUID, SearchRequest> requests) {
        final Map<UUID, Integer> requestOffsets = new HashMap<UUID, Integer>();

        StringBuilder text = new StringBuilder(document);
        for (Map.Entry<UUID, SearchRequest> requestEntry : requests.entrySet()) {
            final SearchRequest request = requestEntry.getValue();
            final String trace = request.getTrace();
            final UUID requestId = requestEntry.getKey();
            assert requestId == request.getSearchInfo().requestId;

            final int traceStartsAt = findStacktraceOffset(text, trace);
            requestOffsets.put(requestId, traceStartsAt);
            if (traceStartsAt >= 0) {
                // Make sure we will not find this part of the document again
                String blank = new String(new char[trace.length()]);
                text = text.replace(traceStartsAt, traceStartsAt + trace.length(), blank);
            }
        }
        return requestOffsets;
    }

    // NOTE using StringBuilder as a parameter to avoid copying the array when it is called in findStacktraceOffsets
    private int findStacktraceOffset(StringBuilder document, String trace) {
        return document.indexOf(trace);
    }

    private SearchMark createMarker(SearchRequest request) {
        final SearchMark mark;
        if (request instanceof RequestedSearch) mark = new RequestedSearchMark((RequestedSearch) request);
        else if (request instanceof SavedSearch) mark = new SavedSearchMark((SavedSearch) request);
        else mark = null;
        return mark;
    }

    private RangeHighlighter addMarkerAtOffset(int offset, SearchMark mark) {
        ApplicationManager.getApplication().assertIsDispatchThread();
        final Document document = editor.getDocument();

        if (0 <= offset && offset < document.getTextLength()) {
            editor.getSettings().setLineMarkerAreaShown(true);
            final int line = document.getLineNumber(offset);
            final MarkupModel markupModel = editor.getMarkupModel();

            RangeHighlighter highlighter;
            highlighter = markupModel.addLineHighlighter(line, HighlighterLayer.ADDITIONAL_SYNTAX, null);
            highlighter.setGutterIconRenderer(mark);
            return highlighter;
        } else {
            return null;
        }
    }
}
