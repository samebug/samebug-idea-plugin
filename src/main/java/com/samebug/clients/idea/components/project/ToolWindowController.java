/*
 * Copyright 2017 Samebug, Inc.
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
import com.samebug.clients.idea.messages.FocusListener;
import com.samebug.clients.idea.messages.RefreshTimestampsListener;
import com.samebug.clients.idea.ui.controller.authentication.AuthenticationController;
import com.samebug.clients.idea.ui.controller.helpRequest.HelpRequestController;
import com.samebug.clients.idea.ui.controller.helpRequestList.HelpRequestListController;
import com.samebug.clients.idea.ui.controller.intro.IntroFrameController;
import com.samebug.clients.idea.ui.controller.solution.SolutionsController;
import com.samebug.clients.swing.ui.modules.MessageService;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

final public class ToolWindowController implements FocusListener, Disposable {
    final static Logger LOGGER = Logger.getInstance(ToolWindowController.class);

    @NotNull
    final Project project;
    IntroFrameController introFrame;
    AuthenticationController authenticationFrame;
    HelpRequestController helpRequestFrame;
    HelpRequestListController helpRequestListFrame;
    SolutionsController solutionFrame;

    @NotNull
    final Timer dateLabelRefresher;


    protected ToolWindowController(@NotNull final Project project) {
        this.project = project;
        MessageBusConnection connection = project.getMessageBus().connect(project);
        connection.subscribe(FocusListener.TOPIC, this);

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
        authenticationFrame = new AuthenticationController(this, project);
        helpRequestListFrame = new HelpRequestListController(this, project);
        helpRequestListFrame.load();

        ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
        Content content;
        if (plugin.getState().apiKey == null) {
            content = contentFactory.createContent(authenticationFrame.getControlPanel(), MessageService.message("samebug.toolwindow.authentication.tabName"), false);
        } else {
            content = contentFactory.createContent(introFrame.getControlPanel(), MessageService.message("samebug.toolwindow.intro.tabName"), false);
        }
        toolWindow.getContentManager().addContent(content);
        toolWindow.getContentManager().addContent(contentFactory.createContent(helpRequestListFrame.getControlPanel(), MessageService.message("samebug.toolwindow.helpRequestList.tabName"), false));
    }

    public void focusOnHelpRequestList() {
        focusOnTab(helpRequestListFrame.getControlPanel(), MessageService.message("samebug.toolwindow.helpRequestList.tabName"));
    }

    public void focusOnHelpRequest(String helpRequestId) {
        final HelpRequestController tab = getOrCreateHelpRequestFrame(helpRequestId);
        focusOnTab(tab.getControlPanel(), MessageService.message("samebug.toolwindow.helpRequest.tabName"));
    }

    @Override
    public void focusOnSearch(final int searchId) {
        // TODO remove other tabs?
        final SolutionsController tab = getOrCreateSolutionFrame(searchId);
        focusOnTab(tab.getControlPanel(), MessageService.message("samebug.toolwindow.search.tabName"));
    }

    @NotNull
    SolutionsController getOrCreateSolutionFrame(final int searchId) {
        ApplicationManager.getApplication().assertIsDispatchThread();

        if (solutionFrame != null && solutionFrame.getSearchId() == searchId) {
            return solutionFrame;
        } else {
            solutionFrame = new SolutionsController(this, project, searchId);
            solutionFrame.load();
            return solutionFrame;
        }
    }

    @NotNull
    HelpRequestController getOrCreateHelpRequestFrame(String helpRequestId) {
        ApplicationManager.getApplication().assertIsDispatchThread();

        if (helpRequestFrame != null && helpRequestFrame.getHelpRequestId().equals(helpRequestId)) {
            return helpRequestFrame;
        } else {
            helpRequestFrame = new HelpRequestController(this, project, helpRequestId);
            helpRequestFrame.load();
            return helpRequestFrame;
        }
    }

    @Override
    public void dispose() {
        if (introFrame != null) Disposer.dispose(introFrame);
        if (authenticationFrame != null) Disposer.dispose(authenticationFrame);
        if (helpRequestListFrame != null) Disposer.dispose(helpRequestListFrame);

//        closeAllSolutionFrames();
//        closeAllHelpRequestFrames();
    }

    public void closeSolutionFrame(final int searchId) {
        if (solutionFrame != null && solutionFrame.getSearchId() == searchId) Disposer.dispose(solutionFrame);
    }

    public void closeAllSolutionFrames() {
        if (solutionFrame != null) Disposer.dispose(solutionFrame);
    }

    public void closeHelpRequestFrame(final String helpRequestId) {
        if (helpRequestFrame != null && helpRequestFrame.getHelpRequestId().equals(helpRequestId)) Disposer.dispose(helpRequestFrame);
    }

    public void closeAllHelpRequestFrames() {
        if (helpRequestFrame != null) Disposer.dispose(helpRequestFrame);
    }

    private void focusOnTab(JComponent tab, String tabTitle) {
        ApplicationManager.getApplication().assertIsDispatchThread();
        final ToolWindow toolWindow = getToolWindow();
        final ContentManager toolwindowCM = toolWindow.getContentManager();

        Content toolWindowTab = toolwindowCM.getContent(tab);
        if (toolWindowTab != null) toolwindowCM.setSelectedContent(toolWindowTab);
        else {
            ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
            Content newToolWindowTab = contentFactory.createContent(tab, tabTitle, false);
            toolwindowCM.addContent(newToolWindowTab);
            toolwindowCM.setSelectedContent(newToolWindowTab);
        }
        toolWindow.show(null);
    }

    private ToolWindow getToolWindow() {
        ToolWindow toolWindow = ToolWindowManager.getInstance(project).getToolWindow("Samebug");
        if (toolWindow instanceof ToolWindowImpl) {
            ((ToolWindowImpl) toolWindow).ensureContentInitialized();
        }
        return toolWindow;
    }

}

