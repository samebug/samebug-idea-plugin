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
package com.samebug.clients.idea.ui.controller.popupController;

import com.intellij.notification.impl.NotificationsManagerImpl;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.popup.Balloon;
import com.intellij.openapi.ui.popup.BalloonBuilder;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.wm.IdeFrame;
import com.intellij.ui.awt.RelativePoint;
import com.samebug.clients.common.ui.component.popup.IIncomingChatInvitationPopup;
import com.samebug.clients.common.ui.modules.TrackingService;
import com.samebug.clients.http.entities.notification.ChatInvitation;
import com.samebug.clients.idea.components.application.IdeaSamebugPlugin;
import com.samebug.clients.idea.notifications.IncomingChatInvitationNotification;
import com.samebug.clients.idea.ui.controller.toolwindow.ToolWindowController;
import com.samebug.clients.swing.tracking.SwingRawEvent;
import com.samebug.clients.swing.ui.component.popup.IncomingChatInvitationPopup;
import com.samebug.clients.swing.ui.modules.ColorService;
import com.samebug.clients.swing.ui.modules.ListenerService;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

//  TODO extract common parts with the other popup controller
public final class IncomingChatInvitationPopupController {
    final ToolWindowController twc;
    final Project myProject;

    public IncomingChatInvitationPopupController(ToolWindowController twc, Project project) {
        this.twc = twc;
        this.myProject = project;
    }

    public void showIncomingChatInvitation(@NotNull ChatInvitation chatInvitation, @NotNull IncomingChatInvitationNotification notification) {
        IncomingChatInvitationPopup.Model popupModel = IdeaSamebugPlugin.getInstance().conversionService.convertIncomingChatInvitationPopup(chatInvitation);
        IncomingChatInvitationPopup popup = new IncomingChatInvitationPopup(popupModel);
        IncomingChatInvitationPopupListener incomingChatInvitationPopupListener = new IncomingChatInvitationPopupListener(this, chatInvitation);
        ListenerService.putListenerToComponent(popup, IIncomingChatInvitationPopup.Listener.class, incomingChatInvitationPopupListener);

        BalloonBuilder balloonBuilder = JBPopupFactory.getInstance().createBalloonBuilder(popup);
        balloonBuilder.setFillColor(ColorService.forCurrentTheme(ColorService.Background));
        balloonBuilder.setContentInsets(new Insets(40, 0, 40, 0));
        balloonBuilder.setBorderInsets(new Insets(0, 0, 0, 0));
        balloonBuilder.setBorderColor(ColorService.forCurrentTheme(ColorService.Background));
        balloonBuilder.setShadow(true);
        IdeFrame window = (IdeFrame) NotificationsManagerImpl.findWindowForBalloon(myProject);
        RelativePoint pointToShowPopup = null;
        if (window != null) pointToShowPopup = RelativePoint.getSouthEastOf(window.getComponent());
        Balloon balloon = balloonBuilder.createBalloon();
        balloon.show(pointToShowPopup, Balloon.Position.atLeft);

        TrackingService.trace(SwingRawEvent.notificationShow(popup));
    }
}
