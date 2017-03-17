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

import com.intellij.notification.NotificationDisplayType;
import com.intellij.notification.impl.NotificationsConfigurationImpl;
import com.intellij.notification.impl.NotificationsManagerImpl;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.popup.Balloon;
import com.intellij.openapi.ui.popup.BalloonBuilder;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.util.Disposer;
import com.intellij.openapi.wm.IdeFrame;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.openapi.wm.impl.ToolWindowImpl;
import com.intellij.ui.awt.RelativePoint;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import com.intellij.ui.content.ContentManager;
import com.intellij.util.messages.MessageBusConnection;
import com.samebug.clients.common.api.entities.helpRequest.HelpRequest;
import com.samebug.clients.common.ui.component.popup.IHelpRequestPopup;
import com.samebug.clients.idea.components.application.IdeaSamebugPlugin;
import com.samebug.clients.idea.messages.FocusListener;
import com.samebug.clients.idea.messages.IncomingHelpRequest;
import com.samebug.clients.idea.messages.RefreshTimestampsListener;
import com.samebug.clients.idea.notifications.IncomingHelpRequestNotification;
import com.samebug.clients.idea.ui.controller.authentication.AuthenticationController;
import com.samebug.clients.idea.ui.controller.frame.BaseFrameController;
import com.samebug.clients.idea.ui.controller.helpRequest.HelpRequestController;
import com.samebug.clients.idea.ui.controller.helpRequestList.HelpRequestListController;
import com.samebug.clients.idea.ui.controller.solution.SolutionsController;
import com.samebug.clients.idea.ui.modules.IdeaDataService;
import com.samebug.clients.swing.ui.component.popup.HelpRequestPopup;
import com.samebug.clients.swing.ui.modules.ColorService;
import com.samebug.clients.swing.ui.modules.DataService;
import com.samebug.clients.swing.ui.modules.MessageService;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

final public class ToolWindowController implements FocusListener, IncomingHelpRequest, Disposable {
    final static Logger LOGGER = Logger.getInstance(ToolWindowController.class);

    @NotNull
    final Project project;
    ToolWindow toolWindow;
    BaseFrameController currentFrame;

    @NotNull
    final Timer dateLabelRefresher;
    final HelpRequestPopupListener helpRequestPopupListener;


    public ToolWindowController(@NotNull final Project project) {
        this.project = project;
        MessageBusConnection connection = project.getMessageBus().connect(project);
        connection.subscribe(FocusListener.TOPIC, this);
        connection.subscribe(IncomingHelpRequest.TOPIC, this);

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

        helpRequestPopupListener = new HelpRequestPopupListener(this);
    }

    public void initToolWindow(@NotNull ToolWindow toolWindow) {
        this.toolWindow = toolWindow;
        IdeaSamebugPlugin plugin = IdeaSamebugPlugin.getInstance();

        if (plugin.getState().apiKey == null) focusOnAuthentication();
        else focusOnHelpRequestList();
    }

    public void focusOnAuthentication() {
        AuthenticationController controller = new AuthenticationController(this, project);
        openTab(controller, MessageService.message("samebug.toolwindow.authentication.tabName"));
    }

    public void focusOnHelpRequestList() {
        HelpRequestListController controller = new HelpRequestListController(this, project);
        controller.load();
        openTab(controller, MessageService.message("samebug.toolwindow.helpRequestList.tabName"));
    }

    public void focusOnHelpRequest(String helpRequestId) {
        HelpRequestController controller = new HelpRequestController(this, project, helpRequestId);
        controller.load();
        openTab(controller, MessageService.message("samebug.toolwindow.helpRequest.tabName"));
    }

    @Override
    public void focusOnSearch(final int searchId) {
        SolutionsController controller = new SolutionsController(this, project, searchId);
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

    private void openTab(BaseFrameController controller, String tabTitle) {
        ApplicationManager.getApplication().assertIsDispatchThread();
        final ToolWindow toolWindow = getToolWindow();
        final ContentManager toolwindowCM = toolWindow.getContentManager();
        final ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();

        // clean up previous tab
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
        // TODO somewhy the content of the tab does not show up first, only after some interaction (clicking the tab title again, resize toolwindow, etc).
        // Not sure if it is bug in intellij ContentManagerImpl.setSelectedContent() or I'm missing something.
        // This requestFocus seems to fix it, but
        //   - I don't know why
        //   - I don't know if it has any side effects
        toolwindowCM.requestFocus(newToolWindowTab, true);
        toolWindow.show(null);
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

    @Override
    public void showHelpRequest(HelpRequest helpRequest) {
        IncomingHelpRequestNotification n = new IncomingHelpRequestNotification(helpRequest);
        NotificationDisplayType notificationType = NotificationsConfigurationImpl.getSettings(n.getGroupId()).getDisplayType();
        if (NotificationDisplayType.BALLOON == notificationType) {
            // This is the type we set by default.
            // In this case, do not use it as a notification, but create instead a custom balloon and show that, because we cannot customize the presentation of a notification
            IHelpRequestPopup.Model popupModel = IdeaSamebugPlugin.getInstance().conversionService.convertHelpRequestPopup(helpRequest);
            HelpRequestPopup popup = new HelpRequestPopup(popupModel);
            DataService.putData(popup, IdeaDataService.Project, project);

            BalloonBuilder bb = JBPopupFactory.getInstance().createBalloonBuilder(popup);
            bb.setFillColor(ColorService.forCurrentTheme(ColorService.Background));
            bb.setContentInsets(new Insets(10, 10, 10, 10));
            IdeFrame f = (IdeFrame) NotificationsManagerImpl.findWindowForBalloon(project);
            RelativePoint x = null;
            if (f != null) x = RelativePoint.getSouthEastOf(f.getComponent());
            Balloon b = bb.createBalloon();
            helpRequestPopupListener.addIncomingHelpRequest(helpRequest, n, b, popup);
            b.show(x, Balloon.Position.atLeft);
        } else {
            // if the user changed it, than handle it as a well-behaved notification
            n.notify(project);
        }
    }

    @Override
    public void addHelpRequest(HelpRequest helpRequest) {
        // nothing to do
    }
}

