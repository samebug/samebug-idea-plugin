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
package com.samebug.clients.idea.ui.controller.helpRequest;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.util.messages.MessageBusConnection;
import com.samebug.clients.common.services.HelpRequestStore;
import com.samebug.clients.common.ui.component.community.IHelpOthersCTA;
import com.samebug.clients.common.ui.component.hit.IWebHit;
import com.samebug.clients.common.ui.component.profile.IProfilePanel;
import com.samebug.clients.common.ui.frame.IFrame;
import com.samebug.clients.common.ui.frame.helpRequest.IHelpRequestFrame;
import com.samebug.clients.common.ui.frame.solution.IWebResultsTab;
import com.samebug.clients.http.entities.helprequest.HelpRequest;
import com.samebug.clients.http.entities.profile.UserInfo;
import com.samebug.clients.http.entities.profile.UserStats;
import com.samebug.clients.http.entities.response.GetSolutions;
import com.samebug.clients.http.entities.response.GetTips;
import com.samebug.clients.http.entities.response.IncomingHelpRequestList;
import com.samebug.clients.idea.components.application.IdeaSamebugPlugin;
import com.samebug.clients.idea.messages.IncomingHelpRequest;
import com.samebug.clients.idea.messages.RefreshTimestampsListener;
import com.samebug.clients.idea.ui.controller.component.ProfileListener;
import com.samebug.clients.idea.ui.controller.component.WebHitListener;
import com.samebug.clients.idea.ui.controller.component.WebResultsTabListener;
import com.samebug.clients.idea.ui.controller.externalEvent.ProfileUpdateListener;
import com.samebug.clients.idea.ui.controller.externalEvent.RefreshListener;
import com.samebug.clients.idea.ui.controller.frame.BaseFrameController;
import com.samebug.clients.idea.ui.controller.toolwindow.ToolWindowController;
import com.samebug.clients.swing.ui.frame.helpRequest.HelpRequestFrame;
import com.samebug.clients.swing.ui.modules.ListenerService;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.concurrent.Future;

public final class HelpRequestController extends BaseFrameController<IHelpRequestFrame> implements Disposable {
    @NotNull
    final String helpRequestId;

    final RefreshListener refreshListener;
    final ProfileUpdateListener profileUpdateListener;
    final HelpRequestFrameListener frameListener;
    final ProfileListener profileListener;
    final WriteTipListener writeTipListener;
    WebHitListener webHitListener;
    WebResultsTabListener webResultsTabListener;

    final HelpRequestStore helpRequestStore;


    public HelpRequestController(ToolWindowController twc, Project project, @NotNull final String helpRequestId) {
        super(twc, project, new HelpRequestFrame());
        this.helpRequestId = helpRequestId;

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

        MessageBusConnection projectConnection = myProject.getMessageBus().connect(this);
        refreshListener = new RefreshListener(this);
        projectConnection.subscribe(RefreshTimestampsListener.TOPIC, refreshListener);
        profileUpdateListener = new ProfileUpdateListener(this);
        projectConnection.subscribe(IncomingHelpRequest.TOPIC, profileUpdateListener);
    }

    public String getHelpRequestId() {
        return helpRequestId;
    }


    public void load() {
        // TODO every controllers should also make sure to set the loading screen when starting to load content
        view.setLoading();

        webHitListener = null;
        ListenerService.removeListenerFromComponent((JComponent) view, IWebHit.Listener.class);
        webResultsTabListener = null;
        ListenerService.removeListenerFromComponent((JComponent) view, IWebResultsTab.Listener.class);

        final Future<UserInfo> userInfoTask = concurrencyService.userInfo();
        final Future<UserStats> userStatsTask = concurrencyService.userStats();
        final Future<IncomingHelpRequestList> incomingHelpRequestsTask = concurrencyService.incomingHelpRequests(false);
        // TODO here I should get a help request match?
        final Future<HelpRequest> helpRequestTask = concurrencyService.helpRequest(helpRequestId);

        load(helpRequestTask, incomingHelpRequestsTask, userInfoTask, userStatsTask);
    }

    /**
     * Wait for the help request so we can decide which search id to use for showing the solutions
     */
    private void load(final Future<HelpRequest> helpRequestTask,
                      final Future<IncomingHelpRequestList> incomingHelpRequestsTask,
                      final Future<UserInfo> userInfoTask,
                      final Future<UserStats> userStatsTask) {
        new LoadingTask() {
            @Override
            protected void load() throws Exception {
                HelpRequest helpRequest = helpRequestTask.get();
                int accessibleSearchId = helpRequest.getSearchId();

                webResultsTabListener = new WebResultsTabListener(accessibleSearchId);
                ListenerService.putListenerToComponent((JComponent) view, IWebResultsTab.Listener.class, webResultsTabListener);
                webHitListener = new WebHitListener(accessibleSearchId);
                ListenerService.putListenerToComponent((JComponent) view, IWebHit.Listener.class, webHitListener);

                final Future<GetSolutions> solutionsTask = concurrencyService.solutions(accessibleSearchId);
                final Future<GetTips> tipsTask = concurrencyService.tips(accessibleSearchId);
                HelpRequestController.this.load(solutionsTask, tipsTask, helpRequestTask, incomingHelpRequestsTask, userInfoTask, userStatsTask);
            }
        }.executeInBackground();

    }

    private void load(final Future<GetSolutions> solutionsTask,
                      final Future<GetTips> tipsTask,
                      final Future<HelpRequest> helpRequestTask,
                      final Future<IncomingHelpRequestList> incomingHelpRequestsTask,
                      final Future<UserInfo> userInfoTask,
                      final Future<UserStats> userStatsTask) {
        new LoadingTask() {
            @Override
            protected void load() throws Exception {
                final IHelpRequestFrame.Model model = conversionService.convertHelpRequestFrame(tipsTask.get().getData(), solutionsTask.get().getData(),
                        helpRequestTask.get(), incomingHelpRequestsTask.get(), userInfoTask.get(), userStatsTask.get());
                ApplicationManager.getApplication().invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        view.loadingSucceeded(model);
                    }
                });
            }
        }.executeInBackground();
    }
}
