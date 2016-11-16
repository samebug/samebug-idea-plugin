/**
 * Copyright 2016 Samebug, Inc.
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
package com.samebug.clients.idea.console;

import com.intellij.execution.actions.ConsoleActionsPostProcessor;
import com.intellij.execution.impl.ConsoleViewImpl;
import com.intellij.execution.ui.ConsoleView;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.editor.event.DocumentAdapter;
import com.intellij.openapi.editor.event.DocumentEvent;
import com.intellij.openapi.editor.event.EditorFactoryEvent;
import com.intellij.openapi.editor.event.EditorFactoryListener;
import com.intellij.openapi.editor.impl.EditorImpl;
import com.intellij.openapi.editor.markup.GutterIconRenderer;
import com.intellij.openapi.editor.markup.HighlighterLayer;
import com.intellij.openapi.editor.markup.MarkupModel;
import com.intellij.openapi.editor.markup.RangeHighlighter;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Disposer;
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
import com.samebug.clients.idea.resources.SamebugIcons;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class PostProcessor extends ConsoleActionsPostProcessor {
    public AnAction[] postProcess(@NotNull ConsoleView console, @NotNull AnAction[] actions) {
        if (console instanceof ConsoleViewImpl) {
            ConsoleViewImpl c = (ConsoleViewImpl) console;
            Editor editor = ((ConsoleViewImpl) console).getEditor();
            if (editor instanceof EditorImpl) {
                ConsoleWatcher consoleWatcher = new ConsoleWatcher(c, editor);

                Disposable parent = ((EditorImpl) editor).getDisposable();

                editor.getDocument().addDocumentListener(consoleWatcher, parent);
                editor.getSettings().setLineMarkerAreaShown(true);
            }
        }

        return actions;
    }
}

class ConsoleWatcher extends DocumentAdapter implements SearchRequestListener {
    private final Logger LOGGER = Logger.getInstance(ConsoleWatcher.class);
    private final Editor editor;
    private final ConsoleViewImpl c;
    private final RequestService requestService;
    private final Map<UUID, RangeHighlighter> highlights;

    public ConsoleWatcher(ConsoleViewImpl c, Editor editor) {
        Project project = editor.getProject();
        this.c = c;
        this.editor = editor;
        this.requestService = project.getComponent(SamebugProjectComponent.class).getRequestService();
        this.highlights = new ConcurrentHashMap<UUID, RangeHighlighter>();

        Disposable parent = ((EditorImpl) editor).getDisposable();

        MessageBusConnection messageBusConnection = project.getMessageBus().connect(parent);
        messageBusConnection.subscribe(SearchRequestListener.TOPIC, this);
    }

    @Override
    public void documentChanged(DocumentEvent e) {
        rebuildMarkers();
    }

    @Override
    public void saved(final UUID requestId, final Saved savedSearch) {
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
                for (UUID h : highlights.keySet()) {
                    LOGGER.info("current highlight: " + h);
                }

                for (RangeHighlighter h : highlights.values()) {
                    h.dispose();
                }
                highlights.clear();

                for (Map.Entry<Integer, UUID> foundRequest : foundRequests.entrySet()) {
                    LOGGER.info("Found requests: " + foundRequest.getValue());
                    final RangeHighlighter highlight;
                    int line = foundRequest.getKey();
                    UUID requestId = foundRequest.getValue();
                    SearchRequest request = requestService.getRequest(requestId);
                    LOGGER.info("Adding marker for request " + requestId + " to line " + line);
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
        ApplicationManager.getApplication().assertIsDispatchThread();

        final MarkupModel markupModel = editor.getMarkupModel();
        RangeHighlighter highlighter;
        highlighter = markupModel.addLineHighlighter(line, HighlighterLayer.ADDITIONAL_SYNTAX, null);
        highlighter.setGutterIconRenderer(new RequestedSearchMark());
        return highlighter;
    }


    private RangeHighlighter addSavedSearchMarker(int line, Saved request) {
        LOGGER.info("place saved marker in line " + line);
        ApplicationManager.getApplication().assertIsDispatchThread();

        final MarkupModel markupModel = editor.getMarkupModel();
        RangeHighlighter highlighter;
        Integer traceLineOffset = request.getSavedSearch().getFirstLine();
        int correctedLine = traceLineOffset == null ? line : line + traceLineOffset;
        highlighter = markupModel.addLineHighlighter(correctedLine, HighlighterLayer.ADDITIONAL_SYNTAX, null);
        highlighter.setGutterIconRenderer(new SavedSearchMark());
        return highlighter;
    }

    private RangeHighlighter addSearchedSearchMarker(int line, Searched request) {
        ApplicationManager.getApplication().assertIsDispatchThread();

        final MarkupModel markupModel = editor.getMarkupModel();
        RangeHighlighter highlighter = null;
        return highlighter;
    }
}

class RequestedSearchMark extends GutterIconRenderer {
    @NotNull
    @Override
    public Icon getIcon() {
        return SamebugIcons.reload;
    }

    @Override
    public boolean equals(Object o) {
        return false;
    }

    @Override
    public int hashCode() {
        return 0;
    }
}

class SavedSearchMark extends GutterIconRenderer {
    @NotNull
    @Override
    public Icon getIcon() {
        return SamebugIcons.gutterSamebug;
    }

    @Override
    public boolean equals(Object o) {
        return false;
    }

    @Override
    public int hashCode() {
        return 0;
    }
}
