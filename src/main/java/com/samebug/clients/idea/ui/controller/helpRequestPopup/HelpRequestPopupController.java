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
package com.samebug.clients.idea.ui.controller.helpRequestPopup;

import com.intellij.notification.impl.NotificationsManagerImpl;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.popup.Balloon;
import com.intellij.openapi.ui.popup.BalloonBuilder;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.wm.IdeFrame;
import com.intellij.ui.awt.RelativePoint;
import com.samebug.clients.common.ui.component.popup.IHelpRequestPopup;
import com.samebug.clients.http.entities.helpRequest.HelpRequest;
import com.samebug.clients.idea.components.application.IdeaSamebugPlugin;
import com.samebug.clients.idea.notifications.IncomingHelpRequestNotification;
import com.samebug.clients.idea.tracking.Events;
import com.samebug.clients.idea.ui.controller.toolwindow.ToolWindowController;
import com.samebug.clients.swing.ui.component.popup.HelpRequestPopup;
import com.samebug.clients.swing.ui.modules.ColorService;
import com.samebug.clients.swing.ui.modules.ListenerService;
import com.samebug.clients.swing.ui.modules.TrackingService;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public final class HelpRequestPopupController {
    final ToolWindowController twc;
    final Project myProject;

    final Map<IHelpRequestPopup, HelpRequest> data;
    final Map<IHelpRequestPopup, IncomingHelpRequestNotification> notifications;
    final Map<IHelpRequestPopup, Balloon> balloons;

    public HelpRequestPopupController(ToolWindowController twc, Project project) {
        this.twc = twc;
        this.myProject = project;
        data = new HashMap<IHelpRequestPopup, HelpRequest>();
        notifications = new HashMap<IHelpRequestPopup, IncomingHelpRequestNotification>();
        balloons = new HashMap<IHelpRequestPopup, Balloon>();
    }


    public void showIncomingHelpRequest(@NotNull HelpRequest helpRequest, @NotNull IncomingHelpRequestNotification notification) {
        IHelpRequestPopup.Model popupModel = IdeaSamebugPlugin.getInstance().conversionService.convertHelpRequestPopup(helpRequest);
        HelpRequestPopup popup = new HelpRequestPopup(popupModel);

        HelpRequestPopupListener helpRequestPopupListener = new HelpRequestPopupListener(this);
        ListenerService.putListenerToComponent(popup, IHelpRequestPopup.Listener.class, helpRequestPopupListener);

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
        data.put(popup, helpRequest);
        notifications.put(popup, notification);
        balloons.put(popup, balloon);
        balloon.show(pointToShowPopup, Balloon.Position.atLeft);

        TrackingService.trace(Events.helpRequestNotificationShow(helpRequest.id));
    }

    void hideAndRemoveIncomingHelpRequest(@NotNull IHelpRequestPopup view) {
        Balloon balloon = balloons.get(view);
        IncomingHelpRequestNotification notification = notifications.get(view);

        assert balloon != null;
        assert notification != null;
        notification.expire();
        balloon.hide();

        balloons.remove(view);
        notifications.remove(view);
        data.remove(view);
    }
}
