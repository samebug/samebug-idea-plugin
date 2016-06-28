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
package com.samebug.clients.idea.components.project;

import com.intellij.openapi.actionSystem.DataKey;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.AbstractProjectComponent;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import com.intellij.ui.content.ContentManager;
import com.intellij.util.messages.MessageBusConnection;
import com.samebug.clients.idea.messages.view.FocusListener;
import com.samebug.clients.idea.resources.SamebugBundle;
import com.samebug.clients.idea.ui.controller.HistoryTabController;
import com.samebug.clients.idea.ui.controller.search.SearchTabController;
import com.samebug.clients.idea.ui.controller.TabController;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

final public class ToolWindowController extends AbstractProjectComponent implements FocusListener {
    final static Logger LOGGER = Logger.getInstance(ToolWindowController.class);
    public static final DataKey<TabController> DATA_KEY = DataKey.create("samebugTabController");

    @NotNull
    final Project project;
    @NotNull
    final HistoryTabController historyTabController;
    @NotNull
    final ConcurrentMap<Integer, SearchTabController> solutionControllers;

    @Nullable
    Integer focusedSearch = null;


    protected ToolWindowController(Project project) {
        super(project);
        this.project = project;
        historyTabController = new HistoryTabController(this, project);
        solutionControllers = new ConcurrentHashMap<Integer, SearchTabController>();

        MessageBusConnection projectMessageBus = project.getMessageBus().connect(project);
        projectMessageBus.subscribe(FocusListener.TOPIC, this);
    }

    public void initToolWindow(@NotNull ToolWindow toolWindow) {
        historyTabController.reload();
        ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
        Content content = contentFactory.createContent(historyTabController.getControlPanel(), SamebugBundle.message("samebug.toolwindow.history.tabName"), false);
        toolWindow.getContentManager().addContent(content);
    }

    @Override
    public void focusOnHistory() {
        ApplicationManager.getApplication().assertIsDispatchThread();
        final ToolWindow toolWindow = ToolWindowManager.getInstance(project).getToolWindow("Samebug");
        final ContentManager toolwindowCM = toolWindow.getContentManager();
        final Content content = toolwindowCM.getContent(historyTabController.getControlPanel());
        if (content != null) toolwindowCM.setSelectedContent(content);
        toolWindow.show(null);
    }

    public void focusOnSearch(final int searchId) {
        ApplicationManager.getApplication().assertIsDispatchThread();
        final ToolWindow toolWindow = ToolWindowManager.getInstance(project).getToolWindow("Samebug");
        final ContentManager toolwindowCM = toolWindow.getContentManager();

        // FIXME: for now, we let at most one search tab, so we close all
        if (focusedSearch != null && !focusedSearch.equals(searchId)) {
            closeSearchTab(focusedSearch);
            Content content = toolwindowCM.getContent(1);
            if (content != null) toolwindowCM.removeContent(content, true);
            focusedSearch = null;
        }

        final SearchTabController tab = getOrCreateSearchTab(searchId);
        focusedSearch = searchId;
        Content toolWindowTab = toolwindowCM.getContent(tab.getControlPanel());
        if (toolWindowTab != null) toolwindowCM.setSelectedContent(toolWindowTab);
        else {
            ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
            Content newToolWindowTab = contentFactory.createContent(tab.getControlPanel(), SamebugBundle.message("samebug.toolwindow.search.tabName"), false);
            toolwindowCM.addContent(newToolWindowTab);
            toolwindowCM.setSelectedContent(newToolWindowTab);
        }
        toolWindow.show(null);
    }

    @NotNull
    SearchTabController getOrCreateSearchTab(final int searchId) {
        ApplicationManager.getApplication().assertIsDispatchThread();
        if (solutionControllers.containsKey(searchId)) {
            return solutionControllers.get(searchId);
        } else {
            final SearchTabController newSearchController = new SearchTabController(this, project, searchId);
            solutionControllers.put(searchId, newSearchController);
            newSearchController.reload();
            return newSearchController;
        }
    }

    // TODO add close action to tab which calls this method
    public void closeSearchTab(final int searchId) {
        solutionControllers.remove(searchId);
    }

}
