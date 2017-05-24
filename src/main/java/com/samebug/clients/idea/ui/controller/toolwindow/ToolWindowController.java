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
package com.samebug.clients.idea.ui.controller.toolwindow;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Disposer;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.openapi.wm.impl.ToolWindowImpl;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import com.intellij.ui.content.ContentManager;
import com.intellij.util.messages.MessageBusConnection;
import com.samebug.clients.common.entities.search.ReadableSearchGroup;
import com.samebug.clients.common.tracking.Funnels;
import com.samebug.clients.common.tracking.Hooks;
import com.samebug.clients.common.ui.modules.MessageService;
import com.samebug.clients.common.ui.modules.TrackingService;
import com.samebug.clients.http.entities.EntityUtils;
import com.samebug.clients.http.entities.helprequest.HelpRequestMatch;
import com.samebug.clients.idea.components.application.IdeaSamebugPlugin;
import com.samebug.clients.idea.messages.FocusListener;
import com.samebug.clients.idea.messages.IncomingHelpRequest;
import com.samebug.clients.idea.messages.IncomingTip;
import com.samebug.clients.idea.messages.RefreshTimestampsListener;
import com.samebug.clients.idea.tracking.IdeaRawEvent;
import com.samebug.clients.idea.ui.controller.authentication.AuthenticationController;
import com.samebug.clients.idea.ui.controller.frame.BaseFrameController;
import com.samebug.clients.idea.ui.controller.helpRequest.HelpRequestController;
import com.samebug.clients.idea.ui.controller.helpRequestList.HelpRequestListController;
import com.samebug.clients.idea.ui.controller.helpRequestPopup.HelpRequestPopupController;
import com.samebug.clients.idea.ui.controller.incomingTipPopup.IncomingTipPopupController;
import com.samebug.clients.idea.ui.controller.solution.SolutionFrameController;
import com.samebug.clients.swing.tracking.SwingRawEvent;
import com.samebug.clients.swing.tracking.TrackingKeys;
import com.samebug.clients.swing.ui.modules.DataService;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public final class ToolWindowController implements FocusListener, Disposable {
    @NotNull
    final Project project;
    ToolWindow toolWindow;
    BaseFrameController currentFrame;

    final Timer dateLabelRefresher;
    final IncomingHelpRequestPopupListener incomingHelpRequestPopupListener;
    final IncomingTipPopupListener incomingTipPopupListener;
    final ConfigChangeListener configChangeListener;

    final HelpRequestPopupController helpRequestPopupController;
    final IncomingTipPopupController incomingTipPopupController;

    public ToolWindowController(@NotNull final Project project) {
        this.project = project;

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

        incomingHelpRequestPopupListener = new IncomingHelpRequestPopupListener(this);
        incomingTipPopupListener = new IncomingTipPopupListener(this);
        configChangeListener = new ConfigChangeListener(this);
        helpRequestPopupController = new HelpRequestPopupController(this, project);
        incomingTipPopupController = new IncomingTipPopupController(this, project);
        MessageBusConnection connection = project.getMessageBus().connect(project);
        connection.subscribe(FocusListener.TOPIC, this);
        connection.subscribe(IncomingHelpRequest.TOPIC, incomingHelpRequestPopupListener);
        connection.subscribe(IncomingTip.TOPIC, incomingTipPopupListener);
    }

    public void initToolWindow(@NotNull ToolWindow toolWindow) {
        this.toolWindow = toolWindow;
        IdeaSamebugPlugin plugin = IdeaSamebugPlugin.getInstance();
        if (plugin.getState().apiKey == null) {
            final String authenticationTransactionId = Funnels.newTransactionId();
            TrackingService.trace(SwingRawEvent.authenticationHookTriggered(authenticationTransactionId, Hooks.Authentication.UNAUTHENTICATED));
            focusOnAuthentication(authenticationTransactionId);
        } else focusOnHelpRequestList();

        TrackingService.trace(IdeaRawEvent.toolWindowOpen(project));
    }

    public void focusOnAuthentication(@NotNull final String transactionId) {
        AuthenticationController controller = new AuthenticationController(this, project);
        DataService.putData((JComponent) controller.view, TrackingKeys.AuthenticationTransaction, transactionId);
        openTab(controller, MessageService.message("samebug.toolwindow.authentication.tabName"));
    }

    public void focusOnHelpRequestList() {
        HelpRequestListController controller = new HelpRequestListController(this, project);
        controller.load();
        openTab(controller, MessageService.message("samebug.toolwindow.helpRequestList.tabName"));
    }

    public void focusOnHelpRequest(String helpRequestId, String transactionId) {
        HelpRequestMatch match = IdeaSamebugPlugin.getInstance().helpRequestStore.getIncomingHelpRequest(helpRequestId);
        if (match != null) {
            ReadableSearchGroup readableGroup = EntityUtils.getReadableStackTraceSearchGroup(match);
            if (readableGroup != null) {
                HelpRequestController controller = new HelpRequestController(this, project, match, readableGroup);
                DataService.putData((JComponent) controller.view, TrackingKeys.WriteTipTransaction, transactionId);
                controller.load();
                openTab(controller, MessageService.message("samebug.toolwindow.helpRequest.tabName"));
            } else {
                currentFrame.view.popupError("samebug.frame.helpRequestList.openHelpRequest.error.notReadable");
            }
        } else {
            currentFrame.view.popupError("samebug.frame.helpRequestList.openHelpRequest.error.gone");
        }
    }

    @Override
    public void focusOnSearch(final int searchId) {
        SolutionFrameController controller = new SolutionFrameController(this, project, searchId);
        controller.load();
        openTab(controller, MessageService.message("samebug.toolwindow.search.tabName"));
    }

    @Override
    public void dispose() {
        if (currentFrame != null) closeTab(currentFrame);
    }

    public void closeTab(@NotNull final BaseFrameController frame) {
        final ToolWindow toolWindow = getToolWindow();
        final ContentManager toolwindowCM = toolWindow.getContentManager();
        // NOTE the dispose: true parameter here will only dispose the content (the JPanel) but not the controller!
        toolwindowCM.removeContent(toolwindowCM.getContent(frame.getControlPanel()), true);
        Disposer.dispose(frame);
    }

    private void openTab(final BaseFrameController controller, String tabTitle) {
        ApplicationManager.getApplication().assertIsDispatchThread();
        final ToolWindow toolWindow = getToolWindow();
        final ContentManager toolwindowCM = toolWindow.getContentManager();
        final ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();

        // clean up previous tab
        boolean toolWindowOpen = currentFrame == null;
        if (currentFrame != null) {
            toolwindowCM.removeContent(toolwindowCM.getContent(currentFrame.getControlPanel()), true);
            Disposer.dispose(currentFrame);
        }

        // add new content
        currentFrame = controller;
        Content newToolWindowTab = contentFactory.createContent(currentFrame.getControlPanel(), tabTitle, false);
        toolwindowCM.addContent(newToolWindowTab);
        toolwindowCM.setSelectedContent(newToolWindowTab);

        // make sure the toolwindow is visible
        toolWindow.show(new Runnable() {
            @Override
            public void run() {
                JComponent view = (JComponent) currentFrame.view;
                view.revalidate();
                view.repaint();
                TrackingService.trace(IdeaRawEvent.toolWindowShowContent(project, controller));
            }
        });
    }

    private ToolWindow getToolWindow() {
        if (toolWindow == null) {
            ToolWindow tw = ToolWindowManager.getInstance(project).getToolWindow("Samebug");
            if (tw instanceof ToolWindowImpl) {
                ((ToolWindowImpl) tw).ensureContentInitialized();
            }
        }
        return toolWindow;
    }
}

