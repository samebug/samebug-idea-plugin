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
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.event.DocumentAdapter;
import com.intellij.openapi.editor.event.DocumentEvent;
import com.intellij.openapi.editor.markup.GutterIconRenderer;
import com.intellij.openapi.editor.markup.HighlighterLayer;
import com.intellij.openapi.editor.markup.MarkupModel;
import com.intellij.openapi.editor.markup.RangeHighlighter;
import com.intellij.openapi.project.Project;
import com.intellij.util.containers.ArrayListSet;
import com.intellij.util.messages.MessageBusConnection;
import com.samebug.clients.common.services.SearchStore;
import com.samebug.clients.idea.components.project.SamebugProjectComponent;
import com.samebug.clients.idea.messages.console.SearchFinishedListener;
import com.samebug.clients.idea.resources.SamebugIcons;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class PostProcessor extends ConsoleActionsPostProcessor {
    public AnAction[] postProcess(@NotNull ConsoleView console, @NotNull AnAction[] actions) {
        Editor editor = ((ConsoleViewImpl) console).getEditor();
        ConsoleWatcher consoleWatcher = new ConsoleWatcher(editor);

        editor.getDocument().addDocumentListener(consoleWatcher);
        editor.getSettings().setLineMarkerAreaShown(true);
        return actions;
    }
}

class ConsoleWatcher extends DocumentAdapter implements SearchFinishedListener {
    private final Editor editor;
    private final SearchStore searchStore;
    private final Map<UUID, RangeHighlighter> highlights;

    public ConsoleWatcher(Editor editor) {
        Project project = editor.getProject();
        this.editor = editor;
        this.searchStore = project.getComponent(SamebugProjectComponent.class).getSearchStore();
        this.highlights = new ConcurrentHashMap<UUID, RangeHighlighter>();

        MessageBusConnection messageBusConnection = project.getMessageBus().connect(project);
        messageBusConnection.subscribe(SearchFinishedListener.TOPIC, this);
    }

    @Override
    public void documentChanged(DocumentEvent e) {
        finishedProcessing();
    }

    @Override
    public synchronized void finishedProcessing() {
        Document document = editor.getDocument();
        final MarkupModel markupModel = editor.getMarkupModel();
        Collection<UUID> lostRequests = new ArrayListSet<UUID>();
        lostRequests.addAll(highlights.keySet());
        for (RangeHighlighter h : highlights.values()) {
            h.dispose();
        }
        highlights.clear();
        StringBuilder text = new StringBuilder(document.getText());
        for (Map.Entry<UUID, String> traceEntry : searchStore.getTraces().entrySet()) {
            final String trace = traceEntry.getValue();
            final int traceStartsAt = text.indexOf(trace);
            final UUID requestId = traceEntry.getKey();
            if (traceStartsAt >= 0) {
                final int traceLine = document.getLineNumber(traceStartsAt);
                ApplicationManager.getApplication().invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        RangeHighlighter highlighter = markupModel.addLineHighlighter(traceLine, HighlighterLayer.ADDITIONAL_SYNTAX, null);
                        highlighter.setGutterIconRenderer(new GutterIconRenderer() {
                            @NotNull
                            @Override
                            public Icon getIcon() {
                                return SamebugIcons.twBolt;
                            }

                            @Override
                            public boolean equals(Object o) {
                                return false;
                            }

                            @Override
                            public int hashCode() {
                                return 0;
                            }
                        });
                        highlights.put(requestId, highlighter);
                    }
                });

                // Save to cache that this trace was found at that line
                lostRequests.remove(requestId);
                // Make sure we will not find this part of the document again
                String blank = new String(new char[trace.length()]);
                text = text.replace(traceStartsAt, traceStartsAt + trace.length(), blank);
            }
        }
        for (UUID lostRequestId : lostRequests) {
            searchStore.removeRequest(lostRequestId);
        }
    }
}
