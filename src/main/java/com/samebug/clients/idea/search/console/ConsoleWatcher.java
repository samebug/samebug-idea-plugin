/*
 * Copyright 2018 Samebug, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 *    http://www.apache.org/licenses/LICENSE-2.0
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
import java.util.Timer;
import java.util.TimerTask;
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
 */
public class ConsoleWatcher extends DocumentAdapter implements SearchRequestListener {
    private final Logger LOGGER = Logger.getInstance(ConsoleWatcher.class);
    private final int REBUILD_MARKERS_DELAY_IN_MS = 100;

    private final DebugSessionInfo sessionInfo;
    private final Editor editor;
    private final SearchRequestStore searchRequestStore;
    private final Map<UUID, RangeHighlighter> highlights;
    private final Timer timer;
    @Nullable
    private RebuildMarkersTask currentTask;

    public ConsoleWatcher(Project project, ConsoleViewImpl console, DebugSessionInfo sessionInfo) {
        this.sessionInfo = sessionInfo;
        this.editor = console.getEditor();
        this.searchRequestStore = IdeaSamebugPlugin.getInstance().searchRequestStore;
        this.highlights = new ConcurrentHashMap<UUID, RangeHighlighter>();
        this.timer = new Timer("Samebug-ConsoleWatcher-" + sessionInfo.id);
        currentTask = null;
        LOGGER.info("Watcher constructed for " + editor.toString());

        editor.getDocument().addDocumentListener(this, console);
        MessageBusConnection messageBusConnection = project.getMessageBus().connect(console);
        messageBusConnection.subscribe(SearchRequestListener.TOPIC, this);
    }

    @Override
    public void documentChanged(DocumentEvent e) {
        initiateRebuildingMarkers();
    }

    @Override
    public void newSearchRequest(final RequestedSearch requestedSearch) {
        if (sessionInfo.equals(requestedSearch.getSearchInfo().sessionInfo)) initiateRebuildingMarkers();
    }

    @Override
    public void savedSearch(final SavedSearch savedSearch) {
        if (sessionInfo.equals(savedSearch.getSearchInfo().sessionInfo)) initiateRebuildingMarkers();
    }

    @Override
    public void failedSearch(final SearchInfo searchInfo) {
        if (sessionInfo.equals(searchInfo.sessionInfo)) initiateRebuildingMarkers();
    }

    /**
     * Schedule a task to remove all gutter icons and create them from scratch.
     * <p>
     * This is called something changes that might influence the gutter icons:
     * - the content of the console (the editor's document) is changed
     * - there is a new information about a search (e.g. a stack trace is found and posted to Samebug, or the solutions for a search are available)
     * <p>
     * The short delay before executing the rebuild is kind of an optimization. When the application logs a stack trace, the console watcher will be
     * triggered at least three times:
     * - a document update
     * - new search request
     * - search saved/failed
     * <p>
     * The first two of these usually happens close together in time (few millis), and it would be wasteful to make the full rebuild each time.
     * Also, when there is a burst of exceptions, we don't have to rebuild the whole console every time.
     */
    private synchronized void initiateRebuildingMarkers() {
        if (currentTask != null) currentTask.cancel();
        currentTask = new RebuildMarkersTask();
        timer.schedule(currentTask, REBUILD_MARKERS_DELAY_IN_MS);
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

    private static Map<UUID, Integer> findStacktraceOffsets(String document, Map<UUID, SearchRequest> requests) {
        final Map<UUID, Integer> requestOffsets = new HashMap<UUID, Integer>();

        StringBuilder text = new StringBuilder(document);
        for (Map.Entry<UUID, SearchRequest> requestEntry : requests.entrySet()) {
            final SearchRequest request = requestEntry.getValue();
            final String trace = request.getTrace();
            final UUID requestId = requestEntry.getKey();
            assert requestId == request.getSearchInfo().requestId;

            final int traceStartsAt = findStackTraceOffset(text, trace);
            // Make sure we will not find this part of the document again
            if (traceStartsAt >= 0) text = eliminateTrace(text, traceStartsAt, trace.length());
            requestOffsets.put(requestId, traceStartsAt);
        }
        return requestOffsets;
    }

    private static int findStackTraceOffset(StringBuilder text, String trace) {
        return text.indexOf(trace);
    }

    private static StringBuilder eliminateTrace(StringBuilder text, int traceStartOffset, int traceLength) {
        String blank = new String(new char[traceLength]);
        text.replace(traceStartOffset, traceStartOffset + traceLength, blank);
        return text;
    }

    private static SearchMark createMarker(SearchRequest request) {
        final SearchMark mark;
        if (request instanceof RequestedSearch) mark = new RequestedSearchGutterIcon((RequestedSearch) request);
        else if (request instanceof SavedSearch) mark = new SavedSearchGutterIcon((SavedSearch) request);
        else mark = null;
        return mark;
    }

    /**
     * Start a background task to find the stack traces in the console. Then start a task on the UI thread to show these markers
     */
    final class RebuildMarkersTask extends TimerTask {
        @Override
        public void run() {
            ApplicationManager.getApplication().executeOnPooledThread(new Runnable() {
                @Override
                public void run() {
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
            });
        }
    }
}
