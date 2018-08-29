/*
 * Copyright 2018 Samebug, Inc.
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
package com.samebug.clients.idea.ui.controller.helpRequest;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.util.messages.MessageBusConnection;
import com.samebug.clients.common.entities.search.ReadableSearchGroup;
import com.samebug.clients.common.services.HelpRequestStore;
import com.samebug.clients.common.tracking.Locations;
import com.samebug.clients.common.ui.component.community.IHelpOthersCTA;
import com.samebug.clients.common.ui.component.hit.IWebHit;
import com.samebug.clients.common.ui.component.profile.IProfilePanel;
import com.samebug.clients.common.ui.frame.IFrame;
import com.samebug.clients.common.ui.frame.helpRequest.IHelpRequestFrame;
import com.samebug.clients.common.ui.frame.solution.IWebResultsTab;
import com.samebug.clients.common.ui.modules.TrackingService;
import com.samebug.clients.http.entities.helprequest.HelpRequestMatch;
import com.samebug.clients.http.entities.jsonapi.IncomingHelpRequestList;
import com.samebug.clients.http.entities.jsonapi.SolutionList;
import com.samebug.clients.http.entities.jsonapi.TipList;
import com.samebug.clients.http.entities.profile.UserStats;
import com.samebug.clients.http.entities.user.Me;
import com.samebug.clients.idea.components.application.IdeaSamebugPlugin;
import com.samebug.clients.idea.messages.IncomingHelpRequest;
import com.samebug.clients.idea.messages.ProfileUpdate;
import com.samebug.clients.idea.messages.RefreshTimestampsListener;
import com.samebug.clients.idea.messages.WebSocketStatusUpdate;
import com.samebug.clients.idea.ui.controller.component.ProfileListener;
import com.samebug.clients.idea.ui.controller.component.WebHitListener;
import com.samebug.clients.idea.ui.controller.component.WebResultsTabListener;
import com.samebug.clients.idea.ui.controller.externalEvent.ProfileUpdateListener;
import com.samebug.clients.idea.ui.controller.externalEvent.RefreshListener;
import com.samebug.clients.idea.ui.controller.frame.BaseFrameController;
import com.samebug.clients.idea.ui.controller.toolwindow.ToolWindowController;
import com.samebug.clients.swing.tracking.TrackingKeys;
import com.samebug.clients.swing.ui.frame.helpRequest.HelpRequestFrame;
import com.samebug.clients.swing.ui.modules.DataService;
import com.samebug.clients.swing.ui.modules.ListenerService;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.concurrent.Future;

public final class HelpRequestController extends BaseFrameController<IHelpRequestFrame> implements Disposable {
    @NotNull
    final HelpRequestMatch helpRequestMatch;
    @NotNull
    final ReadableSearchGroup readableGroup;

    final RefreshListener refreshListener;
    final ProfileUpdateListener profileUpdateListener;
    final HelpRequestFrameListener frameListener;
    final ProfileListener profileListener;
    final WriteTipListener writeTipListener;
    WebHitListener webHitListener;
    WebResultsTabListener webResultsTabListener;

    final HelpRequestStore helpRequestStore;


    public HelpRequestController(ToolWindowController twc, Project project, @NotNull final HelpRequestMatch helpRequestMatch, @NotNull final ReadableSearchGroup readableGroup) {
        super(twc, project, new HelpRequestFrame());
        this.helpRequestMatch = helpRequestMatch;
        this.readableGroup = readableGroup;

        IdeaSamebugPlugin plugin = IdeaSamebugPlugin.getInstance();
        helpRequestStore = plugin.helpRequestStore;

        JComponent frame = (JComponent) view;
        frameListener = new HelpRequestFrameListener(this);
        ListenerService.putListenerToComponent(frame, IFrame.FrameListener.class, frameListener);
        ListenerService.putListenerToComponent(frame, IHelpRequestFrame.Listener.class, frameListener);

        profileListener = new ProfileListener(this);
        ListenerService.putListenerToComponent(frame, IProfilePanel.Listener.class, profileListener);

        writeTipListener = new WriteTipListener(this);
        ListenerService.putListenerToComponent(frame, IHelpOthersCTA.Listener.class, writeTipListener);

        int accessibleSearchId = helpRequestMatch.getHelpRequest().getSearchId();
        webResultsTabListener = new WebResultsTabListener(accessibleSearchId);
        ListenerService.putListenerToComponent((JComponent) view, IWebResultsTab.Listener.class, webResultsTabListener);
        webHitListener = new WebHitListener(accessibleSearchId);
        ListenerService.putListenerToComponent((JComponent) view, IWebHit.Listener.class, webHitListener);

        MessageBusConnection projectConnection = myProject.getMessageBus().connect(this);
        refreshListener = new RefreshListener(this);
        projectConnection.subscribe(RefreshTimestampsListener.TOPIC, refreshListener);
        profileUpdateListener = new ProfileUpdateListener(this);
        projectConnection.subscribe(IncomingHelpRequest.TOPIC, profileUpdateListener);
        projectConnection.subscribe(ProfileUpdate.TOPIC, profileUpdateListener);
        projectConnection.subscribe(WebSocketStatusUpdate.TOPIC, profileUpdateListener);
    }

    @NotNull
    public HelpRequestMatch getHelpRequestMatch() {
        return helpRequestMatch;
    }

    @NotNull
    public ReadableSearchGroup getReadableSearchGroup() {
        return readableGroup;
    }

    public void load() {
        view.setLoading();

        final int accessibleSearchId = readableGroup.getLastSearchId();
        final Future<Me> userInfoTask = concurrencyService.userInfo();
        final Future<UserStats> userStatsTask = concurrencyService.userStats();
        final Future<IncomingHelpRequestList> incomingHelpRequestsTask = concurrencyService.incomingHelpRequests(false);
        final Future<SolutionList> solutionsTask = concurrencyService.solutions(accessibleSearchId);
        final Future<TipList> tipsTask = concurrencyService.tips(accessibleSearchId);
        // IMPROVE: even if we have the help request received in constructor, we DO load it from the web to trigger status change from 'unread' to 'read'
        concurrencyService.helpRequest(helpRequestMatch.getHelpRequest().getId());

        new LoadingTask() {
            @Override
            protected void load() throws Exception {
                final IHelpRequestFrame.Model model = conversionService.convertHelpRequestFrame(tipsTask.get().getData(), solutionsTask.get().getData(),
                        helpRequestMatch, incomingHelpRequestsTask.get(), userInfoTask.get(), userStatsTask.get());
                ApplicationManager.getApplication().invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        JComponent frame = (JComponent) view;
                        DataService.putData(frame, TrackingKeys.Location, new Locations.HelpRequest(helpRequestMatch.getHelpRequest().getId()));
                        DataService.putData(frame, TrackingKeys.PageViewId, TrackingService.newPageViewId());
                        view.loadingSucceeded(model);
                    }
                });
            }
        }.executeInBackground();
    }
}
