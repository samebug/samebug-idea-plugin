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
package com.samebug.clients.idea.components.project;

import com.intellij.openapi.actionSystem.DataKey;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.AbstractProjectComponent;
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
import com.samebug.clients.common.search.api.exceptions.SamebugClientException;
import com.samebug.clients.common.services.HistoryService;
import com.samebug.clients.common.services.SolutionService;
import com.samebug.clients.idea.components.application.IdeaSamebugPlugin;
import com.samebug.clients.idea.messages.controller.CloseListener;
import com.samebug.clients.idea.messages.view.FocusListener;
import com.samebug.clients.idea.messages.view.RefreshTimestampsListener;
import com.samebug.clients.idea.resources.SamebugBundle;
import com.samebug.clients.idea.resources.SamebugIcons;
import com.samebug.clients.idea.ui.controller.TabController;
import com.samebug.clients.idea.ui.controller.expsearch.SolutionFrameController;
import com.samebug.clients.idea.ui.controller.history.HistoryFrameController;
import com.samebug.clients.idea.ui.controller.intro.IntroFrameController;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

final public class ToolWindowController extends AbstractProjectComponent implements FocusListener, CloseListener {
    final static Logger LOGGER = Logger.getInstance(ToolWindowController.class);
    public static final DataKey<TabController> DATA_KEY = DataKey.create("samebugTabController");

    @NotNull
    final Project project;
    @Nullable
    IntroFrameController introFrame;
    @Nullable
    HistoryFrameController historyFrame;
    @NotNull
    final ConcurrentMap<Integer, SolutionFrameController> solutionFrames;

    @NotNull
    final Timer dateLabelRefresher;

    @Nullable
    Integer focusedSearch = null;


    protected ToolWindowController(@NotNull final Project project) {
        super(project);
        this.project = project;
        solutionFrames = new ConcurrentHashMap<Integer, SolutionFrameController>();

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
        HistoryService historyService = plugin.getHistoryService();

        if (historyService == null) {
            LOGGER.error("HistoryService was not initialized!");
        } else {
            introFrame = new IntroFrameController(this, project);
            historyFrame = new HistoryFrameController(this, project, historyService);
            ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
            Content content = contentFactory.createContent(historyFrame.getControlPanel(), SamebugBundle.message("samebug.toolwindow.history.tabName"), false);
            toolWindow.getContentManager().addContent(content);
        }
    }

    @Override
    public void focusOnIntro() {
        ApplicationManager.getApplication().assertIsDispatchThread();
        final ToolWindow toolWindow = getToolWindow();
        final ContentManager toolwindowCM = toolWindow.getContentManager();
        assert introFrame != null;
        final Content content = toolwindowCM.getContent(introFrame.getControlPanel());
        if (content != null) toolwindowCM.setSelectedContent(content);
        toolWindow.show(null);
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

        final SolutionFrameController tab = getOrCreateSolutionFrame(searchId);
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
    SolutionFrameController getOrCreateSolutionFrame(final int searchId) {
        IdeaSamebugPlugin plugin = IdeaSamebugPlugin.getInstance();
        SolutionService solutionService = plugin.getSolutionService();

        ApplicationManager.getApplication().assertIsDispatchThread();
        assert solutionService != null;

        if (solutionFrames.containsKey(searchId)) {
            return solutionFrames.get(searchId);
        } else {
            final SolutionFrameController newSolutionFrame = new SolutionFrameController(this, project, solutionService, searchId);
            solutionFrames.put(searchId, newSolutionFrame);
            try {
                solutionService.getSolutions(searchId);
            } catch (SamebugClientException e) {
                // TODO
                e.printStackTrace();
            }
            return newSolutionFrame;
        }
    }

    @Override
    public void disposeComponent() {
        if (introFrame != null) Disposer.dispose(introFrame);
        if (historyFrame != null) Disposer.dispose(historyFrame);

        for (Integer searchId : solutionFrames.keySet()) {
            closeSolutionFrame(searchId);
        }
    }

    @Override
    public void closeSolutionFrame(final int searchId) {
        SolutionFrameController tab = solutionFrames.get(searchId);
        if (tab != null) Disposer.dispose(tab);
        solutionFrames.remove(searchId);
    }

    public void changeToolwindowIcon(boolean hasNewExceptions) {
        ApplicationManager.getApplication().assertIsDispatchThread();
        ToolWindow toolWindow = getToolWindow();
        if (toolWindow != null) {
            if (hasNewExceptions) {
                toolWindow.setIcon(SamebugIcons.twBolt);
            } else {
                toolWindow.setIcon(SamebugIcons.twSamebug);
            }
        }
    }

    private ToolWindow getToolWindow() {
        ToolWindow toolWindow = ToolWindowManager.getInstance(project).getToolWindow("Samebug");
        if (toolWindow instanceof ToolWindowImpl) {
            ((ToolWindowImpl) toolWindow).ensureContentInitialized();
        }
        return toolWindow;
    }

}

