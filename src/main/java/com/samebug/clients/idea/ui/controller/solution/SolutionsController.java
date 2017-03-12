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
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.util.concurrency.FixedFuture;
import com.intellij.util.messages.MessageBus;
import com.samebug.clients.common.api.WebUrlBuilder;
import com.samebug.clients.common.api.entities.*;
import com.samebug.clients.common.api.entities.bugmate.Bugmate;
import com.samebug.clients.common.api.entities.bugmate.BugmatesResult;
import com.samebug.clients.common.api.entities.solution.*;
import com.samebug.clients.common.api.exceptions.*;
import com.samebug.clients.common.services.*;
import com.samebug.clients.common.ui.component.bugmate.IBugmateHit;
import com.samebug.clients.common.ui.component.bugmate.IBugmateList;
import com.samebug.clients.common.ui.component.community.IHelpOthersCTA;
import com.samebug.clients.common.ui.component.hit.IMarkButton;
import com.samebug.clients.common.ui.component.hit.ITipHit;
import com.samebug.clients.common.ui.component.hit.IWebHit;
import com.samebug.clients.common.ui.component.profile.IProfilePanel;
import com.samebug.clients.common.ui.frame.solution.*;
import com.samebug.clients.idea.components.application.IdeaSamebugPlugin;
import com.samebug.clients.idea.components.project.ToolWindowController;
import com.samebug.clients.idea.ui.controller.frame.ConnectionStatusController;
import com.samebug.clients.idea.ui.modules.IdeaDataService;
import com.samebug.clients.swing.ui.frame.solution.SolutionFrame;
import com.samebug.clients.swing.ui.modules.DataService;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.ide.PooledThreadExecutor;

import javax.swing.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public final class SolutionsController implements Disposable {
    final static Logger LOGGER = Logger.getInstance(SolutionsController.class);
    final ToolWindowController twc;
    final Project myProject;

    final int searchId;
    final ConnectionStatusController connectionStatusController;

    final ISolutionFrame view;

    final ViewController viewController;
    final ExceptionHeaderController exceptionHeaderController;
    final WebResultsTabController webResultsTabController;
    final BugmateListController bugmateListController;
    final HelpOthersCTAController helpOthersCTAController;
    final WebHitController webHitController;
    final MarkController markController;
    final ProfileController profileController;
    final SolutionFrameController solutionFrameController;

    private final WebUrlBuilder urlBuilder;
    private final ProfileStore profileStore;
    private final ProfileService profileService;
    private final SolutionStore solutionStore;
    private final SolutionService solutionService;
    private final BugmateStore bugmateStore;
    private final BugmateService bugmateService;

    public SolutionsController(ToolWindowController twc, Project project, final int searchId) {
        this.twc = twc;
        this.myProject = project;
        this.searchId = searchId;
        view = new SolutionFrame();
        DataService.putData((SolutionFrame) view, IdeaDataService.Project, project);

        MessageBus messageBus = myProject.getMessageBus();
        connectionStatusController = new ConnectionStatusController(view, messageBus);

        viewController = new ViewController(this);
        exceptionHeaderController = new ExceptionHeaderController(this);
        webResultsTabController = new WebResultsTabController(this);
        bugmateListController = new BugmateListController(this);
        helpOthersCTAController = new HelpOthersCTAController(this);
        profileController = new ProfileController(this);
        markController = new MarkController(this);
        webHitController = new WebHitController(this);
        solutionFrameController = new SolutionFrameController(this);

        IdeaSamebugPlugin plugin = IdeaSamebugPlugin.getInstance();
        urlBuilder = plugin.urlBuilder;
        solutionStore = plugin.solutionStore;
        profileStore = plugin.profileStore;
        profileService = plugin.profileService;
        solutionService = plugin.solutionService;
        bugmateStore = plugin.bugmateStore;
        bugmateService = plugin.bugmateService;
    }

    public int getSearchId() {
        return searchId;
    }

    public void loadAll() {
        // NOTE I use PooledThreadExecutor.INSTANCE instead of executeOnPooledThread because that logs and swallows the error in the Future
        final Future<Solutions> solutionsTask = PooledThreadExecutor.INSTANCE.submit(new Callable<Solutions>() {
            @Override
            public Solutions call() throws SamebugClientException {
                return solutionService.loadSolutions(searchId);
            }
        });
        final Future<BugmatesResult> bugmatesTask = PooledThreadExecutor.INSTANCE.submit(new Callable<BugmatesResult>() {
            @Override
            public BugmatesResult call() throws SamebugClientException {
                return bugmateService.loadBugmates(searchId);
            }
        });
        final Future<UserInfo> userInfoTask = PooledThreadExecutor.INSTANCE.submit(new Callable<UserInfo>() {
            @Override
            public UserInfo call() throws SamebugClientException {
                return profileService.loadUserInfo();
            }
        });
        final Future<UserStats> userStatsTask = PooledThreadExecutor.INSTANCE.submit(new Callable<UserStats>() {
            @Override
            public UserStats call() throws SamebugClientException {
                return profileService.loadUserStats();
            }
        });

        load(solutionsTask, bugmatesTask, userInfoTask, userStatsTask);
    }

    public void loadLazy() {
        final Future<UserInfo> userInfoTask = new FixedFuture<UserInfo>(profileStore.getUser());
        final Future<UserStats> userStatsTask = new FixedFuture<UserStats>(profileStore.getUserStats());
        final Future<Solutions> solutionsTask = new FixedFuture<Solutions>(solutionStore.get(searchId));
        final Future<BugmatesResult> bugmatesTask = new FixedFuture<BugmatesResult>(bugmateStore.get(searchId));

        load(solutionsTask, bugmatesTask, userInfoTask, userStatsTask);
    }

    private void load(final Future<Solutions> solutionsTask,
                      final Future<BugmatesResult> bugmatesTask,
                      final Future<UserInfo> userInfoTask,
                      final Future<UserStats> userStatsTask) {
        ApplicationManager.getApplication().executeOnPooledThread(new Runnable() {
            @Override
            public void run() {
                try {
                    try {
                        final Solutions solutions = solutionsTask.get();
                        final BugmatesResult bugmates = bugmatesTask.get();
                        final UserInfo user = userInfoTask.get();
                        final UserStats statistics = userStatsTask.get();
                        if (solutions == null) throw new IllegalStateException("solutions was null");
                        else if (bugmates == null) throw new IllegalStateException("bugmates was null");
                        else if (user == null) throw new IllegalStateException("user was null");
                        else if (statistics == null) throw new IllegalStateException("statistics was null");
                        else {
                            final SolutionFrame.Model model = convertSolutionFrame(solutions, bugmates, user, statistics);
                            ApplicationManager.getApplication().invokeLater(new Runnable() {
                                @Override
                                public void run() {
                                    view.loadingSucceeded(model);
                                }
                            });
                        }
                    } catch (IllegalStateException e) {
                        // TODO generic error, probably safe to retry (loadAll)
                        LOGGER.warn("Failed to load user beforehand", e);
                        ApplicationManager.getApplication().invokeLater(new Runnable() {
                            @Override
                            public void run() {
                                view.loadingFailedWithGenericError();
                            }
                        });
                    } catch (InterruptedException e) {
                        // TODO generic error, probably safe to retry (loadLazy)
                        LOGGER.warn("Loading solutions frame interrupted", e);
                        ApplicationManager.getApplication().invokeLater(new Runnable() {
                            @Override
                            public void run() {
                                view.loadingFailedWithGenericError();
                            }
                        });
                    } catch (ExecutionException e) {
                        if (e.getCause() instanceof SamebugClientException) throw (SamebugClientException) e.getCause();
                        else {
                            // TODO generic error, probably safe to retry (loadLazy)
                            LOGGER.warn("Plugin-side error during loading solutions", e);
                            ApplicationManager.getApplication().invokeLater(new Runnable() {
                                @Override
                                public void run() {
                                    view.loadingFailedWithGenericError();
                                }
                            });
                        }
                    }
                } catch (final SamebugClientException e) {
                    // TODO error with loading, bad connection, bad apikey, server error, etc (loadAll)
                    LOGGER.warn("Error during loading solutions", e);
                    ApplicationManager.getApplication().invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            if (e instanceof SamebugTimeout) view.loadingFailedWithRetriableError();
                            else if (e instanceof UserUnauthenticated) view.loadingFailedWithAuthenticationError();
                            else if (e instanceof UserUnauthorized) view.loadingFailedWithAuthorizationError();
                            else if (e instanceof UnsuccessfulResponseStatus && ((UnsuccessfulResponseStatus) e).statusCode == 500) view.loadingFailedWithServerError();
                            else if (e instanceof HttpError) view.loadingFailedWithNetworkError();
                            else view.loadingFailedWithGenericError();
                        }
                    });
                }
            }
        });
    }

    @NotNull
    public JComponent getControlPanel() {
        return (SolutionFrame) view;
    }

    @Override
    public void dispose() {
        connectionStatusController.dispose();
    }

    IMarkButton.Model convertMarkResponse(MarkResponse response) {
        return new IMarkButton.Model(response.getDocumentVotes(), response.getId(), true /*TODO*/);
    }

    IMarkButton.Model convertRetractedMarkResponse(MarkResponse response) {
        return new IMarkButton.Model(response.getDocumentVotes(), null, true /*TODO*/);
    }

    IMarkButton.Model convertMarkPanel(RestHit hit) {
        return new IMarkButton.Model(hit.getScore(), hit.getMarkId(), true /*TODO*/);
    }

    ITipHit.Model convertTipHit(RestHit<Tip> hit) {
        Tip tip = hit.getSolution();
        IMarkButton.Model mark = convertMarkPanel(hit);
        UserReference author = hit.getCreatedBy();
        ITipHit.Model tipHit = new ITipHit.Model(tip.getTip(), hit.getSolutionId(), tip.getCreatedAt(), author.getDisplayName(), author.getAvatarUrl(), mark);
        return tipHit;
    }

    ISolutionFrame.Model convertSolutionFrame(@NotNull Solutions solutions, @NotNull BugmatesResult bugmates, @NotNull UserInfo user, @NotNull UserStats statistics) {
        final List<IWebHit.Model> webHits = new ArrayList<IWebHit.Model>(solutions.getReferences().size());
        for (RestHit<SolutionReference> externalHit : solutions.getReferences()) {
            SolutionReference externalSolution = externalHit.getSolution();
            IMarkButton.Model mark = convertMarkPanel(externalHit);
            final String sourceIconName = externalSolution.getSource().getIcon();
            final URL sourceIconUrl = urlBuilder.sourceIcon(sourceIconName);

            String createdBy = null;
            if (externalSolution.getAuthor() != null) createdBy = externalSolution.getAuthor().getName();
            IWebHit.Model webHit =
                    new IWebHit.Model(externalSolution.getTitle(), externalSolution.getUrl(), externalHit.getSolutionId(),
                            externalSolution.getCreatedAt(), createdBy,
                            externalSolution.getSource().getName(), sourceIconUrl,
                            mark);
            webHits.add(webHit);
        }

        IWebResultsTab.Model webResults = new IWebResultsTab.Model(webHits);
        IHelpOthersCTA.Model cta = new IHelpOthersCTA.Model(0);
        final List<ITipHit.Model> tipHits = new ArrayList<ITipHit.Model>(solutions.getTips().size());
        for (RestHit<Tip> tipSolution : solutions.getTips()) {
            ITipHit.Model tipHit = convertTipHit(tipSolution);
            tipHits.add(tipHit);
        }
        final List<IBugmateHit.Model> bugmateHits = new ArrayList<IBugmateHit.Model>(bugmates.getBugmates().size());
        for (Bugmate b : bugmates.getBugmates()) {
            IBugmateHit.Model model = new IBugmateHit.Model(b.getUserId(), b.getDisplayName(), b.getAvatarUrl(), b.getNumberOfSearches(), b.getLastSeen());
            bugmateHits.add(model);
        }
        String exceptionTitle = SolutionService.headLine(solutions.getSearchGroup().getLastSearch());
        IBugmateList.Model bugmateList = new IBugmateList.Model(bugmateHits, bugmates.getNumberOfOtherBugmates(), bugmates.isEvenMoreExists(), exceptionTitle);
        ITipResultsTab.Model tipResults = new ITipResultsTab.Model(tipHits, bugmateList);
        IResultTabs.Model resultTabs = new IResultTabs.Model(webResults, tipResults, cta);
        ISearchHeaderPanel.Model header = new ISearchHeaderPanel.Model(exceptionTitle);
        IProfilePanel.Model profile =
                new IProfilePanel.Model(0, statistics.getNumberOfMarks(), statistics.getNumberOfTips(), statistics.getNumberOfThanks(),
                        user.getDisplayName(), user.getAvatarUrl());
        ISolutionFrame.Model model = new ISolutionFrame.Model(resultTabs, header, profile);

        return model;
    }

}

