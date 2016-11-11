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

import com.intellij.execution.actions.ConsoleActionsPostProcessor;
import com.intellij.execution.impl.ConsoleViewImpl;
import com.intellij.execution.ui.ConsoleView;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorGutterAction;
import com.intellij.openapi.editor.colors.ColorKey;
import com.intellij.openapi.editor.colors.EditorFontType;
import com.intellij.openapi.editor.event.DocumentAdapter;
import com.intellij.openapi.editor.event.DocumentEvent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vcs.actions.ActiveAnnotationGutter;
import com.intellij.util.containers.ArrayListSet;
import com.intellij.util.messages.MessageBusConnection;
import com.samebug.clients.common.services.SearchStore;
import com.samebug.clients.idea.components.application.Tracking;
import com.samebug.clients.idea.components.project.SamebugProjectComponent;
import com.samebug.clients.idea.messages.console.SearchFinishedListener;
import com.samebug.clients.idea.messages.view.FocusListener;
import com.samebug.clients.idea.tracking.Events;
import com.samebug.clients.search.api.entities.SearchResults;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class PostProcessor extends ConsoleActionsPostProcessor {
    public AnAction[] postProcess(@NotNull ConsoleView console, @NotNull AnAction[] actions) {
        Editor editor = ((ConsoleViewImpl) console).getEditor();
        SearchCache cache = new SearchCache();
        Project project = editor.getProject();
        ConsoleWatcher consoleWatcher = new ConsoleWatcher(editor, cache);
        Annotation annotation = new Annotation(editor.getProject(), cache);

        editor.getDocument().addDocumentListener(consoleWatcher);
        editor.getGutter().registerTextAnnotation(annotation, annotation);
        return actions;
    }
}

class ConsoleWatcher extends DocumentAdapter implements SearchFinishedListener {
    private final Editor editor;
    private final SearchStore searchStore;
    private final SearchCache cache;

    public ConsoleWatcher(Editor editor, SearchCache cache) {
        Project project = editor.getProject();
        this.editor = editor;
        this.cache = cache;
        this.searchStore = project.getComponent(SamebugProjectComponent.class).getSearchStore();

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
        Collection<UUID> lostRequests = new ArrayListSet<UUID>();
        lostRequests.addAll(cache.values());
        cache.clear();
        StringBuilder text = new StringBuilder(document.getText());
        for (Map.Entry<UUID, String> traceEntry : searchStore.getTraces().entrySet()) {
            String trace = traceEntry.getValue();
            int traceStartsAt = text.indexOf(trace);
            if (traceStartsAt >= 0) {
                // Save to cache that this trace was found at that line
                int traceLine = document.getLineNumber(traceStartsAt);
                cache.put(traceLine, traceEntry.getKey());
                lostRequests.remove(traceEntry.getKey());
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

class Annotation implements ActiveAnnotationGutter, EditorGutterAction {
    private final Project myProject;
    private final SearchCache cache;
    private final SearchStore searchStore;

    public Annotation(Project project, SearchCache cache) {
        myProject = project;
        this.cache = cache;
        this.searchStore = project.getComponent(SamebugProjectComponent.class).getSearchStore();
    }

    @Override
    public void doAction(int i) {
        UUID requestId = cache.get(i);
        if (requestId != null) {
            SearchResults result = searchStore.getResult(requestId);
            if (result != null) {
                Integer searchId = result.getSearchId();
                myProject.getMessageBus().syncPublisher(FocusListener.TOPIC).focusOnSearch(searchId);
                Tracking.projectTracking(myProject).trace(Events.searchClick(myProject, searchId));
            }
        }
    }

    @Override
    public Cursor getCursor(int i) {
        return null;
    }

    @Nullable
    @Override
    public String getLineText(int i, Editor editor) {
        UUID requestForLine = cache.get(i);
        if (requestForLine != null) {
            return "X";
        } else {
            return null;
        }
    }

    @Nullable
    @Override
    public String getToolTip(int i, Editor editor) {
        UUID requestId = cache.get(i);
        if (requestId != null) {
            SearchResults searchResult = searchStore.getResult(requestId);
            if (searchResult != null) {
                return "Search " + searchResult.getSearchId();
            } else {
                return "Search in progress...";
            }
        } else {
            return null;
        }
    }

    @Override
    public EditorFontType getStyle(int i, Editor editor) {
        return null;
    }

    @Nullable
    @Override
    public ColorKey getColor(int i, Editor editor) {
        return null;
    }

    @Nullable
    @Override
    public Color getBgColor(int i, Editor editor) {
        return null;
    }

    @Override
    public List<AnAction> getPopupActions(int i, Editor editor) {
        return null;
    }

    @Override
    public void gutterClosed() {

    }
}

class SearchCache {
    private final Map<Integer, UUID> searchesByLine;

    public SearchCache() {
        searchesByLine = new ConcurrentHashMap<Integer, UUID>();
    }

    public void clear() {
        searchesByLine.clear();
    }

    public void put(Integer line, UUID requestId) {
        searchesByLine.put(line, requestId);
    }

    public UUID get(Integer line) {
        return searchesByLine.get(line);
    }

    public Collection<UUID> values() {
        return searchesByLine.values();
    }
}
