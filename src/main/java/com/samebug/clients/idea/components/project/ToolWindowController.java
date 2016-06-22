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
import com.samebug.clients.idea.components.application.IdeaSamebugPlugin;
import com.samebug.clients.idea.messages.model.ConnectionStatusListener;
import com.samebug.clients.idea.messages.view.FocusListener;
import com.samebug.clients.idea.messages.view.SearchViewListener;
import com.samebug.clients.idea.resources.SamebugBundle;
import com.samebug.clients.idea.ui.component.tab.SearchTabView;
import com.samebug.clients.idea.ui.controller.HistoryTabController;
import com.samebug.clients.idea.ui.controller.SearchTabController;
import com.samebug.clients.idea.ui.controller.SolutionsController;
import com.samebug.clients.search.api.entities.Solutions;
import com.samebug.clients.search.api.exceptions.SamebugClientException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.HashMap;
import java.util.Map;

public class ToolWindowController extends AbstractProjectComponent implements FocusListener {
    final static Logger LOGGER = Logger.getInstance(ToolWindowController.class);
    @NotNull
    final Project project;
    @NotNull
    final HistoryTabController historyTabController;
    @NotNull
    final SolutionsController solutionsController;
    @Nullable
    Integer focusedSearch = null;


    protected ToolWindowController(Project project) {
        super(project);
        this.project = project;
        solutionsController = new SolutionsController(project);
        historyTabController = new HistoryTabController(project);

        MessageBusConnection projectMessageBus = project.getMessageBus().connect(project);
        projectMessageBus.subscribe(FocusListener.TOPIC, this);
    }

    // TODO remove, leave only getHistoryView, or even move toolwindow initialization in this class from the factory
    @NotNull
    public HistoryTabController getHistoryTabController() {
        return historyTabController;
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
            solutionsController.close(focusedSearch);
            Content content = toolwindowCM.getContent(1);
            if (content != null) toolwindowCM.removeContent(content, true);
            focusedSearch = null;
        }

        SearchTabView tab = solutionsController.getTab(searchId);
        if (tab == null) {
            solutionsController.open(searchId);
        } else {
            focusedSearch = searchId;
            Content toolWindowTab = toolwindowCM.getContent(tab);
            if (toolWindowTab != null) toolwindowCM.setSelectedContent(toolWindowTab);
            else {
                ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
                Content newToolWindowTab = contentFactory.createContent(tab, SamebugBundle.message("samebug.toolwindow.search.tabName"), false);
                toolwindowCM.addContent(newToolWindowTab);
                toolwindowCM.setSelectedContent(newToolWindowTab);
            }
        }
        toolWindow.show(null);
    }

    // TODO add close action to tab which calls this method
    public void closeSearchTab(int searchId) {
    }

}
