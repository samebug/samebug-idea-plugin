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
package com.samebug.clients.idea.components.project;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Disposer;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.openapi.wm.impl.ToolWindowImpl;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import com.intellij.ui.content.ContentManager;
import com.intellij.util.messages.MessageBusConnection;
import com.samebug.clients.idea.components.application.IdeaSamebugPlugin;
import com.samebug.clients.idea.messages.CloseListener;
import com.samebug.clients.idea.messages.FocusListener;
import com.samebug.clients.idea.messages.RefreshTimestampsListener;
import com.samebug.clients.idea.ui.controller.authentication.AuthenticationController;
import com.samebug.clients.idea.ui.controller.intro.IntroFrameController;
import com.samebug.clients.idea.ui.controller.solution.SolutionsController;
import com.samebug.clients.idea.ui.modules.IdeaDataService;
import com.samebug.clients.swing.ui.modules.DataService;
import com.samebug.clients.swing.ui.modules.MessageService;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

final public class ToolWindowController implements FocusListener, CloseListener, Disposable {
    final static Logger LOGGER = Logger.getInstance(ToolWindowController.class);

    @NotNull
    final Project project;
    @Nullable
    IntroFrameController introFrame;
    @Nullable
    AuthenticationController authenticationFrame;
    @NotNull
    final ConcurrentMap<Integer, SolutionsController> solutionFrames;

    @NotNull
    final Timer dateLabelRefresher;

    @Nullable
    Integer focusedSearch = null;


    protected ToolWindowController(@NotNull final Project project) {
        this.project = project;
        solutionFrames = new ConcurrentHashMap<Integer, SolutionsController>();

        MessageBusConnection connection = project.getMessageBus().connect(project);
        connection.subscribe(FocusListener.TOPIC, this);
        connection.subscribe(CloseListener.TOPIC, this);

        final int LabelRefreshInitialDelayInMs = 1 * 60 * 1000;
        final int LabelRefreshDelayInMs = 1 * 60 * 1000;
        dateLabelRefresher = new Timer(LabelRefreshDelayInMs, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!project.isDisposed()) {
                    project.getMessageBus().syncPublisher(RefreshTimestampsListener.TOPIC).refreshDateLabels();
                }
            }
        });
        dateLabelRefresher.setInitialDelay(LabelRefreshInitialDelayInMs);
        dateLabelRefresher.start();
    }

    public void initToolWindow(@NotNull ToolWindow toolWindow) {
        IdeaSamebugPlugin plugin = IdeaSamebugPlugin.getInstance();

        introFrame = new IntroFrameController(this, project);
        DataService.putData(introFrame.getControlPanel(), IdeaDataService.Project, project);

        authenticationFrame = new AuthenticationController(this, project);

        ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
        Content content;
        if (plugin.getState().apiKey == null) {
            content = contentFactory.createContent(authenticationFrame.getControlPanel(), MessageService.message("samebug.toolwindow.authentication.tabName"), false);
        } else {
            content = contentFactory.createContent(introFrame.getControlPanel(), MessageService.message("samebug.toolwindow.intro.tabName"), false);
        }
        toolWindow.getContentManager().addContent(content);
    }

    @Override
    public void focusOnSearch(final int searchId) {
        ApplicationManager.getApplication().assertIsDispatchThread();
        final ToolWindow toolWindow = getToolWindow();
        final ContentManager toolwindowCM = toolWindow.getContentManager();

        // FIXME: for now, we let at most one search tab, so we close all
        if (focusedSearch != null && !focusedSearch.equals(searchId)) {
            project.getMessageBus().syncPublisher(CloseListener.TOPIC).closeSolutionFrame(focusedSearch);
            Content content = toolwindowCM.getContent(1);
            if (content != null) toolwindowCM.removeContent(content, true);
            focusedSearch = null;
        }

        final SolutionsController tab = getOrCreateSolutionFrame(searchId);
        focusedSearch = searchId;
        Content toolWindowTab = toolwindowCM.getContent(tab.getControlPanel());
        if (toolWindowTab != null) toolwindowCM.setSelectedContent(toolWindowTab);
        else {
            ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
            JComponent solutionFrame = tab.getControlPanel();
            Content newToolWindowTab = contentFactory.createContent(solutionFrame, MessageService.message("samebug.toolwindow.search.tabName"), false);
            DataService.putData(solutionFrame, IdeaDataService.Project, project);
            toolwindowCM.addContent(newToolWindowTab);
            toolwindowCM.setSelectedContent(newToolWindowTab);
        }
        toolWindow.show(null);
    }

    @NotNull
    SolutionsController getOrCreateSolutionFrame(final int searchId) {
        ApplicationManager.getApplication().assertIsDispatchThread();

        if (solutionFrames.containsKey(searchId)) {
            return solutionFrames.get(searchId);
        } else {
            final SolutionsController newSolutionFrame = new SolutionsController(this, project, searchId);
            newSolutionFrame.loadAll();
            solutionFrames.put(searchId, newSolutionFrame);
            return newSolutionFrame;
        }
    }

    @Override
    public void dispose() {
        if (introFrame != null) Disposer.dispose(introFrame);
        if (authenticationFrame != null) Disposer.dispose(authenticationFrame);

        for (Integer searchId : solutionFrames.keySet()) {
            closeSolutionFrame(searchId);
        }
    }

    @Override
    public void closeSolutionFrame(final int searchId) {
        SolutionsController tab = solutionFrames.get(searchId);
        if (tab != null) Disposer.dispose(tab);
        solutionFrames.remove(searchId);
    }

    private ToolWindow getToolWindow() {
        ToolWindow toolWindow = ToolWindowManager.getInstance(project).getToolWindow("Samebug");
        if (toolWindow instanceof ToolWindowImpl) {
            ((ToolWindowImpl) toolWindow).ensureContentInitialized();
        }
        return toolWindow;
    }

}

