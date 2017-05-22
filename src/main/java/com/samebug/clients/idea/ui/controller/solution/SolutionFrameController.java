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
package com.samebug.clients.idea.ui.controller.solution;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.util.concurrency.FixedFuture;
import com.intellij.util.messages.MessageBusConnection;
import com.samebug.clients.common.tracking.Location;
import com.samebug.clients.common.ui.component.community.IAskForHelp;
import com.samebug.clients.common.ui.component.community.IHelpOthersCTA;
import com.samebug.clients.common.ui.component.helpRequest.IMyHelpRequest;
import com.samebug.clients.common.ui.component.hit.IMarkButton;
import com.samebug.clients.common.ui.component.hit.IWebHit;
import com.samebug.clients.common.ui.component.profile.IProfilePanel;
import com.samebug.clients.common.ui.frame.IFrame;
import com.samebug.clients.common.ui.frame.solution.ISearchHeaderPanel;
import com.samebug.clients.common.ui.frame.solution.ISolutionFrame;
import com.samebug.clients.common.ui.frame.solution.IWebResultsTab;
import com.samebug.clients.common.ui.modules.TrackingService;
import com.samebug.clients.http.entities.helprequest.HelpRequest;
import com.samebug.clients.http.entities.jsonapi.BugmateList;
import com.samebug.clients.http.entities.jsonapi.IncomingHelpRequestList;
import com.samebug.clients.http.entities.jsonapi.SolutionList;
import com.samebug.clients.http.entities.jsonapi.TipList;
import com.samebug.clients.http.entities.profile.UserStats;
import com.samebug.clients.http.entities.search.Search;
import com.samebug.clients.http.entities.search.SearchGroup;
import com.samebug.clients.http.entities.user.Me;
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
import com.samebug.clients.swing.ui.frame.solution.SolutionFrame;
import com.samebug.clients.swing.ui.modules.DataService;
import com.samebug.clients.swing.ui.modules.ListenerService;

import javax.swing.*;
import java.util.concurrent.Future;

public final class SolutionFrameController extends BaseFrameController<ISolutionFrame> implements Disposable {
    final int searchId;

    final RefreshListener refreshListener;
    final ProfileUpdateListener profileUpdateListener;
    final ExceptionHeaderListener exceptionHeaderListener;
    final WebResultsTabListener webResultsTabListener;
    final RequestHelpListener requestHelpListener;
    final RevokeHelpRequestListener revokeHelpRequestListener;
    final HelpOthersCTAListener helpOthersCTAListener;
    final WebHitListener webHitListener;
    final MarkButtonListener markButtonListener;
    final ProfileListener profileListener;
    final SolutionFrameListener frameListener;

    public SolutionFrameController(ToolWindowController twc, Project project, final int searchId) {
        super(twc, project, new SolutionFrame());
        this.searchId = searchId;

        // bind view listeners
        JComponent frame = (JComponent) view;
        frameListener = new SolutionFrameListener(this);
        ListenerService.putListenerToComponent(frame, IFrame.FrameListener.class, frameListener);
        ListenerService.putListenerToComponent(frame, ISolutionFrame.Listener.class, frameListener);

        exceptionHeaderListener = new ExceptionHeaderListener(this);
        ListenerService.putListenerToComponent(frame, ISearchHeaderPanel.Listener.class, exceptionHeaderListener);

        webResultsTabListener = new WebResultsTabListener(searchId);
        ListenerService.putListenerToComponent(frame, IWebResultsTab.Listener.class, webResultsTabListener);

        webHitListener = new WebHitListener(searchId);
        ListenerService.putListenerToComponent(frame, IWebHit.Listener.class, webHitListener);

        requestHelpListener = new RequestHelpListener(this);
        ListenerService.putListenerToComponent(frame, IAskForHelp.Listener.class, requestHelpListener);

        revokeHelpRequestListener = new RevokeHelpRequestListener(this);
        ListenerService.putListenerToComponent(frame, IMyHelpRequest.Listener.class, revokeHelpRequestListener);

        helpOthersCTAListener = new HelpOthersCTAListener(this);
        ListenerService.putListenerToComponent(frame, IHelpOthersCTA.Listener.class, helpOthersCTAListener);

        profileListener = new ProfileListener(this);
        ListenerService.putListenerToComponent(frame, IProfilePanel.Listener.class, profileListener);

        markButtonListener = new MarkButtonListener(this);
        ListenerService.putListenerToComponent(frame, IMarkButton.Listener.class, markButtonListener);

        // bind external listeners
        MessageBusConnection projectConnection = myProject.getMessageBus().connect(this);
        refreshListener = new RefreshListener(this);
        projectConnection.subscribe(RefreshTimestampsListener.TOPIC, refreshListener);

        profileUpdateListener = new ProfileUpdateListener(this);
        projectConnection.subscribe(IncomingHelpRequest.TOPIC, profileUpdateListener);
        projectConnection.subscribe(ProfileUpdate.TOPIC, profileUpdateListener);
        projectConnection.subscribe(WebSocketStatusUpdate.TOPIC, profileUpdateListener);
    }

    public int getSearchId() {
        return searchId;
    }

    public void load() {
        view.setLoading();

        final Future<Me> userInfoTask = concurrencyService.userInfo();
        final Future<UserStats> userStatsTask = concurrencyService.userStats();
        final Future<IncomingHelpRequestList> incomingHelpRequestsTask = concurrencyService.incomingHelpRequests(false);
        final Future<SolutionList> solutionsTask = concurrencyService.solutions(searchId);
        final Future<TipList> tipsTask = concurrencyService.tips(searchId);
        final Future<BugmateList> bugmatesTask = concurrencyService.bugmates(searchId);
        final Future<Search> searchTask = concurrencyService.search(searchId);

        new LoadingTask() {
            @Override
            protected void load() throws Exception {
                // Wait for the search to see our help request id on that search, so we can load and show that help request.
                Search search = searchTask.get();
                SearchGroup group = search.getGroup();
                String myHelpRequestId = group.getHelpRequestId();

                final Future<HelpRequest> helpRequestTask;
                if (myHelpRequestId == null) helpRequestTask = new FixedFuture<HelpRequest>(null);
                else helpRequestTask = concurrencyService.helpRequest(myHelpRequestId);

                new LoadingTask() {
                    @Override
                    protected void load() throws Exception {
                        final SolutionFrame.Model model =
                                conversionService.solutionFrame(searchTask.get(),
                                        tipsTask.get().getData(), solutionsTask.get().getData(), bugmatesTask.get(), helpRequestTask.get(), incomingHelpRequestsTask.get(),
                                        userInfoTask.get(), userStatsTask.get());
                        ApplicationManager.getApplication().invokeLater(new Runnable() {
                            @Override
                            public void run() {
                                view.loadingSucceeded(model);
                                JComponent frame = (JComponent) view;
                                DataService.putData(frame, TrackingKeys.Location, new Location.Search(searchId));
                                DataService.putData(frame, TrackingKeys.PageViewId, TrackingService.newPageViewId());
                            }
                        });
                    }
                }.executeInBackground();
            }
        }.executeInBackground();
    }
}

