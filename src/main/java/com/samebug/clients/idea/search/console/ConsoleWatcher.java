/**
 * Copyright 2017 Samebug, Inc.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
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
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class ConsoleWatcher extends DocumentAdapter implements SearchRequestListener {
    private final Logger LOGGER = Logger.getInstance(ConsoleWatcher.class);

    private final DebugSessionInfo sessionInfo;
    private final Editor editor;
    private final SearchRequestStore searchRequestStore;
    private final Map<UUID, RangeHighlighter> highlights;

    public ConsoleWatcher(Project project, ConsoleViewImpl console, DebugSessionInfo sessionInfo) {
        this.sessionInfo = sessionInfo;
        this.editor = console.getEditor();
        this.searchRequestStore = IdeaSamebugPlugin.getInstance().getSearchRequestStore();
        this.highlights = new ConcurrentHashMap<UUID, RangeHighlighter>();
        LOGGER.info("Watcher constructed for " + editor.toString());

        editor.getDocument().addDocumentListener(this, console);
        MessageBusConnection messageBusConnection = project.getMessageBus().connect(console);
        messageBusConnection.subscribe(SearchRequestListener.TOPIC, this);
    }

    @Override
    public void documentChanged(DocumentEvent e) {
        rebuildMarkers();
    }

    @Override
    public void newSearchRequest(final RequestedSearch requestedSearch) {
        final UUID requestId = requestedSearch.getSearchInfo().requestId;
        final Document document = editor.getDocument();
        final StringBuilder consoleContent = new StringBuilder(document.getText());
        final int traceOffset = findStacktraceOffset(consoleContent, requestedSearch.getTrace());
        ApplicationManager.getApplication().invokeLater(new Runnable() {
            @Override
            public void run() {
                RangeHighlighter newHighlighter;
                // TODO hide this offset -- line rangecheck somewhere
                if (0 <= traceOffset && traceOffset < document.getTextLength()) {
                    int traceLine = document.getLineNumber(traceOffset);
                    newHighlighter = addRequestedSearchMarker(traceLine, requestedSearch);
                } else {
                    newHighlighter = null;
                }
                if (newHighlighter != null) {
                    // new trace is found in the console, add the marker
                    highlights.put(requestId, newHighlighter);
                } else {
                    // this trace is not found, so we could remove it from request store
                    // However in practice a frequent case is that the document is not yet updated with the new content.
                    // Not deleting won't hurt anything, as a documentUpdate will rebuild every marker, and remove the request if the trace is still not found.
                }
            }
        });
    }

    @Override
    public void savedSearch(final SavedSearch savedSearch) {
        final UUID requestId = savedSearch.getSearchInfo().requestId;
        final Document document = editor.getDocument();
        final RangeHighlighter highlightForRequest = highlights.get(requestId);

        if (highlightForRequest == null) {
            // not sure if this can happen, but after rebuilding we surely have a clean and consistent state
            rebuildMarkers();
        } else {
            // if there is a marker for the requested search, we might have to correct it,
            // i.e. move a few lines down if it turns out that the stacktrace starts not exactly in that line.
            ApplicationManager.getApplication().invokeLater(new Runnable() {
                @Override
                public void run() {
                    int originalStartOffset = highlightForRequest.getStartOffset();
                    if (0 <= originalStartOffset && originalStartOffset < document.getTextLength()) {
                        int originalStartLine = document.getLineNumber(originalStartOffset);
                        Integer correction = savedSearch.getSavedSearch().getFirstLine();
                        int correctedStartLine = originalStartLine;
                        if (correction != null) correctedStartLine += correction;

                        highlightForRequest.dispose();
                        RangeHighlighter newHighlighter = addSavedSearchMarker(correctedStartLine, savedSearch);
                        highlights.put(requestId, newHighlighter);
                    }
                }
            });
        }
    }

    @Override
    public void failedSearch(final SearchInfo searchInfo) {
        // TODO this can be the last mark. if it is, we should hide the empty gutter
        final UUID requestId = searchInfo.requestId;
        LOGGER.info("Failed search from editor " + editor.toString());

        final RangeHighlighter highlightForRequest = highlights.get(requestId);
        if (highlightForRequest != null) {
            ApplicationManager.getApplication().invokeLater(new Runnable() {
                @Override
                public void run() {
                    highlightForRequest.dispose();
                    highlights.remove(requestId);
                }
            });
        }
    }

    private synchronized void rebuildMarkers() {
        LOGGER.info("Replace markers for editor " + editor.toString());

        final Document document = editor.getDocument();
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

                final int documentLenght = document.getTextLength();
                for (Map.Entry<UUID, Integer> requestOffsetEntry : requestOffsets.entrySet()) {
                    final UUID requestId = requestOffsetEntry.getKey();
                    final SearchRequest request = currentRequests.get(requestId);
                    final RangeHighlighter highlight;
                    final int offset = requestOffsetEntry.getValue();
                    assert request != null : "currentRequests and requestOffsets should contain the same keys";
                    if (0 <= offset && offset < documentLenght) {
                        // We found the stacktrace in the content of the console
                        final int line = document.getLineNumber(offset);
                        if (request instanceof RequestedSearch) highlight = addRequestedSearchMarker(line, (RequestedSearch) request);
                        else if (request instanceof SavedSearch) highlight = addSavedSearchMarker(line, (SavedSearch) request);
                        else if (request instanceof Searched) highlight = addSearchedSearchMarker(line, (Searched) request);
                        else highlight = null;
                        if (highlight != null) highlights.put(requestId, highlight);
                    } else {
                        // We have not found the stacktrace in the content of the console.
                        // Probably an other process wrote something in between the lines of the trace, or it was rolled over when exceeding console buffer capacity.
                        searchRequestStore.removeRequest(requestId);
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

    @Nullable
    private RangeHighlighter addRequestedSearchMarker(int line, RequestedSearch request) {
        SearchMark mark = new RequestedSearchMark(request);
        return addMarker(line, mark);
    }


    @Nullable
    private RangeHighlighter addSavedSearchMarker(int line, SavedSearch request) {
        LOGGER.info("Add saved search marker for editor " + editor.toString() + " to line " + line + " for request " + request.toString());
        SearchMark mark = new SavedSearchMark(request);
        return addMarker(line, mark);
    }

    private RangeHighlighter addSearchedSearchMarker(int line, Searched request) {
        return null;
    }

    private RangeHighlighter addMarker(int line, SearchMark mark) {
        ApplicationManager.getApplication().assertIsDispatchThread();
        editor.getSettings().setLineMarkerAreaShown(true);

        if (0 <= line && line <= editor.getDocument().getLineCount()) {
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
