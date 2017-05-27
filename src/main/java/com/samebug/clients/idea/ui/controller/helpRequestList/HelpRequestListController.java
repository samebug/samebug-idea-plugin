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
package com.samebug.clients.idea.ui.controller.helpRequestList;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.util.messages.MessageBusConnection;
import com.samebug.clients.common.tracking.Locations;
import com.samebug.clients.common.ui.component.helpRequest.IHelpRequestPreview;
import com.samebug.clients.common.ui.component.profile.IProfilePanel;
import com.samebug.clients.common.ui.frame.IFrame;
import com.samebug.clients.common.ui.frame.helpRequestList.IHelpRequestList;
import com.samebug.clients.common.ui.frame.helpRequestList.IHelpRequestListFrame;
import com.samebug.clients.common.ui.modules.TrackingService;
import com.samebug.clients.http.entities.jsonapi.IncomingHelpRequestList;
import com.samebug.clients.http.entities.profile.UserStats;
import com.samebug.clients.http.entities.user.Me;
import com.samebug.clients.idea.components.application.IdeaSamebugPlugin;
import com.samebug.clients.idea.messages.IncomingHelpRequest;
import com.samebug.clients.idea.messages.ProfileUpdate;
import com.samebug.clients.idea.messages.RefreshTimestampsListener;
import com.samebug.clients.idea.messages.WebSocketStatusUpdate;
import com.samebug.clients.idea.ui.controller.component.ProfileListener;
import com.samebug.clients.idea.ui.controller.externalEvent.ProfileUpdateListener;
import com.samebug.clients.idea.ui.controller.externalEvent.RefreshListener;
import com.samebug.clients.idea.ui.controller.frame.BaseFrameController;
import com.samebug.clients.idea.ui.controller.toolwindow.ToolWindowController;
import com.samebug.clients.idea.ui.modules.BrowserUtil;
import com.samebug.clients.swing.tracking.TrackingKeys;
import com.samebug.clients.swing.ui.frame.helpRequestList.HelpRequestListFrame;
import com.samebug.clients.swing.ui.modules.DataService;
import com.samebug.clients.swing.ui.modules.ListenerService;

import javax.swing.*;
import java.net.URI;
import java.util.concurrent.Future;

public final class HelpRequestListController extends BaseFrameController<IHelpRequestListFrame> implements Disposable {

    final RefreshListener refreshListener;
    final ProfileUpdateListener profileUpdateListener;
    final HelpRequestListFrameListener frameListener;
    final HelpRequestPreviewListener helpRequestPreviewListener;
    final HelpRequestListListener helpRequestListListener;
    final ProfileListener profileListener;

    public HelpRequestListController(ToolWindowController twc, Project project) {
        super(twc, project, new HelpRequestListFrame());

        JComponent frame = (JComponent) view;

        frameListener = new HelpRequestListFrameListener(this);
        ListenerService.putListenerToComponent(frame, IFrame.FrameListener.class, frameListener);
        ListenerService.putListenerToComponent(frame, IHelpRequestListFrame.Listener.class, frameListener);

        helpRequestPreviewListener = new HelpRequestPreviewListener(this);
        ListenerService.putListenerToComponent(frame, IHelpRequestPreview.Listener.class, helpRequestPreviewListener);

        helpRequestListListener = new HelpRequestListListener();
        ListenerService.putListenerToComponent(frame, IHelpRequestList.Listener.class, helpRequestListListener);

        profileListener = new ProfileListener(this);
        ListenerService.putListenerToComponent(frame, IProfilePanel.Listener.class, profileListener);


        MessageBusConnection projectConnection = myProject.getMessageBus().connect(this);
        refreshListener = new RefreshListener(this);
        projectConnection.subscribe(RefreshTimestampsListener.TOPIC, refreshListener);
        profileUpdateListener = new ProfileUpdateListener(this);
        projectConnection.subscribe(IncomingHelpRequest.TOPIC, profileUpdateListener);
        projectConnection.subscribe(ProfileUpdate.TOPIC, profileUpdateListener);
        projectConnection.subscribe(WebSocketStatusUpdate.TOPIC, profileUpdateListener);
    }

    public void load() {
        view.setLoading();

        final Future<Me> userInfoTask = concurrencyService.userInfo();
        final Future<UserStats> userStatsTask = concurrencyService.userStats();
        final Future<IncomingHelpRequestList> helpRequestsTask = concurrencyService.incomingHelpRequests(true);

        new LoadingTask() {
            @Override
            protected void load() throws Exception {
                final IHelpRequestListFrame.Model model = conversionService.convertHelpRequestListFrame(
                        helpRequestsTask.get(), userInfoTask.get(), userStatsTask.get());
                ApplicationManager.getApplication().invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        JComponent frame = (JComponent) view;
                        DataService.putData(frame, TrackingKeys.Location, new Locations.HelpRequestList());
                        DataService.putData(frame, TrackingKeys.PageViewId, TrackingService.newPageViewId());
                        view.loadingSucceeded(model);
                    }
                });
            }
        }.executeInBackground();
    }


    final class HelpRequestListListener implements IHelpRequestList.Listener {
        @Override
        public void moreClicked() {
            IdeaSamebugPlugin plugin = IdeaSamebugPlugin.getInstance();
            Me user = plugin.profileStore.getUser();
            if (user != null) {
                int myUserId = user.getId();
                URI uri = plugin.uriBuilder.profile(myUserId);
                BrowserUtil.browse(uri);
            }
        }
    }
}
