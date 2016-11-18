/**
 * Copyright 2016 Samebug, Inc.
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
package com.samebug.clients.idea.console;

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
import com.samebug.clients.common.entities.search.Requested;
import com.samebug.clients.common.entities.search.Saved;
import com.samebug.clients.common.entities.search.SearchRequest;
import com.samebug.clients.common.entities.search.Searched;
import com.samebug.clients.common.services.RequestService;
import com.samebug.clients.idea.components.project.SamebugProjectComponent;
import com.samebug.clients.idea.messages.console.SearchRequestListener;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class ConsoleWatcher extends DocumentAdapter implements SearchRequestListener {
    private final Logger LOGGER = Logger.getInstance(ConsoleWatcher.class);

    private final Editor editor;
    private final ConsoleViewImpl console;
    private final RequestService requestService;
    private final Map<UUID, RangeHighlighter> highlights;

    public ConsoleWatcher(ConsoleViewImpl console) {
        this.editor = console.getEditor();
        Project project = editor.getProject();
        this.console = console;
        this.requestService = project.getComponent(SamebugProjectComponent.class).getRequestService();
        this.highlights = new ConcurrentHashMap<UUID, RangeHighlighter>();

        LOGGER.info("Watcher constructed for " + editor.toString());
        editor.getDocument().addDocumentListener(this, console);
        MessageBusConnection messageBusConnection = project.getMessageBus().connect(console);
        messageBusConnection.subscribe(SearchRequestListener.TOPIC, this);
    }

    @Override
    public void documentChanged(DocumentEvent e) {
        LOGGER.info("Document change for editor " + editor.toString());
        rebuildMarkers();
    }

    @Override
    public void saved(final UUID requestId, final Saved savedSearch) {
        LOGGER.info("Saved search from editor " + editor.toString());
        final Document document = editor.getDocument();
        ApplicationManager.getApplication().invokeLater(new Runnable() {
            @Override
            public void run() {
                RangeHighlighter highlightForRequest = highlights.get(requestId);
                if (highlightForRequest != null) {
                    // The stacktrace does not begins at the start of the fragment, we have to move the marker
                    int originalStartOffset = highlightForRequest.getStartOffset();
                    int originalStartLine = document.getLineNumber(originalStartOffset);

                    highlightForRequest.dispose();
                    RangeHighlighter newHighlighter = addSavedSearchMarker(originalStartLine, savedSearch);
                    highlights.put(requestId, newHighlighter);
                }
            }
        });
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
        for (Map.Entry<UUID, SearchRequest> traceEntry : requestService.getRequests().entrySet()) {
            final SearchRequest request = traceEntry.getValue();
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

        for (UUID lostRequestId : lostRequests) {
            requestService.removeRequest(lostRequestId);
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
                    SearchRequest request = requestService.getRequest(requestId);
                    if (request instanceof Requested) highlight = addRequestedSearchMarker(line, (Requested) request);
                    else if (request instanceof Saved) highlight = addSavedSearchMarker(line, (Saved) request);
                    else if (request instanceof Searched) highlight = addSearchedSearchMarker(line, (Searched) request);
                    else highlight = null;
                    if (highlight != null) highlights.put(requestId, highlight);
                }
            }
        });
    }

    private RangeHighlighter addRequestedSearchMarker(int line, Requested request) {
        LOGGER.info("Add request marker for editor " + editor.toString() + " to line " + line + " for request " + request.toString());
        ApplicationManager.getApplication().assertIsDispatchThread();

        final MarkupModel markupModel = editor.getMarkupModel();
        RangeHighlighter highlighter;
        highlighter = markupModel.addLineHighlighter(line, HighlighterLayer.ADDITIONAL_SYNTAX, null);
        return highlighter;
    }


    private RangeHighlighter addSavedSearchMarker(int line, Saved request) {
        LOGGER.info("Add saved search marker for editor " + editor.toString() + " to line " + line + " for request " + request.toString());
        ApplicationManager.getApplication().assertIsDispatchThread();

        editor.getSettings().setLineMarkerAreaShown(true);
        final MarkupModel markupModel = editor.getMarkupModel();
        RangeHighlighter highlighter;
        Integer traceLineOffset = request.getSavedSearch().getFirstLine();
        int correctedLine = traceLineOffset == null ? line : line + traceLineOffset;
        highlighter = markupModel.addLineHighlighter(correctedLine, HighlighterLayer.ADDITIONAL_SYNTAX, null);
        highlighter.setGutterIconRenderer(new SavedSearchMark(request));
        return highlighter;
    }

    private RangeHighlighter addSearchedSearchMarker(int line, Searched request) {
        ApplicationManager.getApplication().assertIsDispatchThread();

        final MarkupModel markupModel = editor.getMarkupModel();
        RangeHighlighter highlighter = null;
        return highlighter;
    }
}
