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
import com.intellij.util.containers.ArrayListSet;
import com.intellij.util.containers.HashMap;
import com.intellij.util.messages.MessageBusConnection;
import com.samebug.clients.common.entities.search.*;
import com.samebug.clients.common.services.SearchRequestStore;
import com.samebug.clients.idea.components.application.IdeaSamebugPlugin;
import com.samebug.clients.idea.components.application.Tracking;
import com.samebug.clients.idea.search.SearchRequestListener;
import com.samebug.clients.idea.tracking.Events;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
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
    public void saved(final UUID requestId, final SavedSearch savedSearch) {
        final Document document = editor.getDocument();
        if (highlights.get(requestId) == null) {
            rebuildMarkers();
        } else {
            ApplicationManager.getApplication().invokeLater(new Runnable() {
                @Override
                public void run() {
                    RangeHighlighter highlightForRequest = highlights.get(requestId);
                    if (highlightForRequest != null) {
                        int originalStartOffset = highlightForRequest.getStartOffset();
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
    public void searched(UUID requestId) {

    }

    @Override
    public void failed(final UUID requestId) {
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

        Document document = editor.getDocument();
        final Collection<UUID> lostRequests = new ArrayListSet<UUID>();
        final Map<Integer, UUID> foundRequests = new HashMap<Integer, UUID>();

        // Remove current highlights, and pretend that all traces are lost from the current document
        lostRequests.addAll(highlights.keySet());

        // Try to find traces requested for search in the document
        StringBuilder text = new StringBuilder(document.getText());
        for (Map.Entry<UUID, SearchRequest> traceEntry : searchRequestStore.getRequests(sessionInfo).entrySet()) {
            final SearchRequest request = traceEntry.getValue();
            if (request != null) {
                final String trace = request.getTrace();
                final int traceStartsAt = text.indexOf(trace);
                final UUID requestId = traceEntry.getKey();
                if (traceStartsAt >= 0) {
                    final int traceLine = document.getLineNumber(traceStartsAt);

                    // Save to cache that this request was found at that line
                    foundRequests.put(traceLine, requestId);
                    lostRequests.remove(requestId);

                    // Make sure we will not find this part of the document again
                    String blank = new String(new char[trace.length()]);
                    text = text.replace(traceStartsAt, traceStartsAt + trace.length(), blank);
                }
            }
        }

        ApplicationManager.getApplication().invokeLater(new Runnable() {
            @Override
            public void run() {
                for (RangeHighlighter h : highlights.values()) {
                    h.dispose();
                }
                highlights.clear();
                editor.getSettings().setLineMarkerAreaShown(false);

                for (Map.Entry<Integer, UUID> foundRequest : foundRequests.entrySet()) {
                    final RangeHighlighter highlight;
                    int line = foundRequest.getKey();
                    UUID requestId = foundRequest.getValue();
                    SearchRequest request = searchRequestStore.getRequest(requestId);
                    if (request instanceof Requested) highlight = addRequestedSearchMarker(line, (Requested) request);
                    else if (request instanceof SavedSearch) highlight = addSavedSearchMarker(line, (SavedSearch) request);
                    else if (request instanceof Searched) highlight = addSearchedSearchMarker(line, (Searched) request);
                    else highlight = null;
                    if (highlight != null) highlights.put(requestId, highlight);
                }
            }
        });
    }

    @Nullable
    private RangeHighlighter addRequestedSearchMarker(int line, Requested request) {
        LOGGER.info("Add request marker for editor " + editor.toString() + " to line " + line + " for request " + request.toString());
        ApplicationManager.getApplication().assertIsDispatchThread();

        if (editor.getDocument().getLineCount() >= line) {
            final MarkupModel markupModel = editor.getMarkupModel();
            RangeHighlighter highlighter;
            highlighter = markupModel.addLineHighlighter(line, HighlighterLayer.ADDITIONAL_SYNTAX, null);
            return highlighter;
        } else {
            return null;
        }
    }


    @Nullable
    private RangeHighlighter addSavedSearchMarker(int line, SavedSearch request) {
        LOGGER.info("Add saved search marker for editor " + editor.toString() + " to line " + line + " for request " + request.toString());
        ApplicationManager.getApplication().assertIsDispatchThread();
        editor.getSettings().setLineMarkerAreaShown(true);

        if (editor.getDocument().getLineCount() >= line) {
            final MarkupModel markupModel = editor.getMarkupModel();
            RangeHighlighter highlighter;
            highlighter = markupModel.addLineHighlighter(line, HighlighterLayer.ADDITIONAL_SYNTAX, null);
            highlighter.setGutterIconRenderer(new SavedSearchMark(request));

            Project project = editor.getProject();
            if (project != null) {
                Tracking.projectTracking(project).trace(Events.gutterIconForSavedSearch(request.getSavedSearch().getSearchId()));
            }

            return highlighter;
        } else {
            return null;
        }
    }

    private RangeHighlighter addSearchedSearchMarker(int line, Searched request) {
        ApplicationManager.getApplication().assertIsDispatchThread();

        final MarkupModel markupModel = editor.getMarkupModel();
        RangeHighlighter highlighter = null;
        return highlighter;
    }
}
