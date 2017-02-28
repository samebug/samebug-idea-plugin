/**
 * Copyright 2017 Samebug, Inc.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
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
import com.samebug.clients.common.search.api.WebUrlBuilder;
import com.samebug.clients.common.search.api.entities.*;
import com.samebug.clients.common.search.api.exceptions.*;
import com.samebug.clients.common.services.*;
import com.samebug.clients.common.ui.component.profile.IProfilePanel;
import com.samebug.clients.common.ui.component.solutions.*;
import com.samebug.clients.idea.components.application.IdeaSamebugPlugin;
import com.samebug.clients.idea.components.project.ToolWindowController;
import com.samebug.clients.swing.ui.component.solutions.SolutionFrame;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.ide.PooledThreadExecutor;

import javax.swing.*;
import java.lang.Exception;
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
    private final BugmateService bugmateService;

    public SolutionsController(ToolWindowController twc, Project project, final int searchId) {
        this.twc = twc;
        this.myProject = project;
        this.searchId = searchId;

        view = new SolutionFrame(myProject.getMessageBus());

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
        bugmateService = plugin.bugmateService;
    }

    public void loadAll() {
        // NOTE I use PooledThreadExecutor.INSTANCE instead of executeOnPooledThread because that logs and swallows the error in the Future
        final Future<Solutions> solutionsTask = PooledThreadExecutor.INSTANCE.submit(new Callable<Solutions>() {
            @Override
            public Solutions call() throws Exception {
                return solutionService.loadSolutions(searchId);
            }
        });
        final Future<BugmatesResult> bugmatesTask = PooledThreadExecutor.INSTANCE.submit(new Callable<BugmatesResult>() {
            @Override
            public BugmatesResult call() throws Exception {
                return bugmateService.loadBugmates(searchId);
            }
        });
        final Future<UserInfo> userInfoTask = PooledThreadExecutor.INSTANCE.submit(new Callable<UserInfo>() {
            @Override
            public UserInfo call() throws Exception {
                return profileService.loadUserInfo();
            }
        });
        final Future<UserStats> userStatsTask = PooledThreadExecutor.INSTANCE.submit(new Callable<UserStats>() {
            @Override
            public UserStats call() throws Exception {
                return profileService.loadUserStats();
            }
        });

        load(solutionsTask, bugmatesTask, userInfoTask, userStatsTask);
    }

    public void loadLazy() {
        final Future<Solutions> solutionsTask = PooledThreadExecutor.INSTANCE.submit(new Callable<Solutions>() {
            @Override
            public Solutions call() throws Exception {
                return solutionService.loadSolutions(searchId);
            }
        });
        final Future<BugmatesResult> bugmatesTask = PooledThreadExecutor.INSTANCE.submit(new Callable<BugmatesResult>() {
            @Override
            public BugmatesResult call() throws Exception {
                return bugmateService.loadBugmates(searchId);
            }
        });

        final Future<UserInfo> userInfoTask;
        final Future<UserStats> userStatsTask;
        final UserInfo userInfo = profileStore.getUser();
        final UserStats userStats = profileStore.getUserStats();
        if (userInfo != null) userInfoTask = new FixedFuture<UserInfo>(userInfo);
        else userInfoTask = FixedFuture.completeExceptionally(new IllegalStateException(""));
        if (userStats != null) userStatsTask = new FixedFuture<UserStats>(userStats);
        else userStatsTask = FixedFuture.completeExceptionally(new IllegalStateException(""));

        load(solutionsTask, bugmatesTask, userInfoTask, userStatsTask);
    }

    private void load(final Future<Solutions> solutionsTask, final Future<BugmatesResult> bugmatesTask, final Future<UserInfo> userInfoTask, final Future<UserStats> userStatsTask) {
        ApplicationManager.getApplication().executeOnPooledThread(new Runnable() {
            @Override
            public void run() {
                try {
                    try {
                        final Solutions solutions = solutionsTask.get();
                        final BugmatesResult bugmates = bugmatesTask.get();
                        final UserInfo user = userInfoTask.get();
                        final UserStats statistics = userStatsTask.get();
                        final SolutionFrame.Model model = convertSolutionFrame(solutions, bugmates, user, statistics);
                        ApplicationManager.getApplication().invokeLater(new Runnable() {
                            @Override
                            public void run() {
                                view.loadingSucceeded(model);
                            }
                        });
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

    ISolutionFrame.Model convertSolutionFrame(@NotNull Solutions solutions, @NotNull BugmatesResult bugmates, @NotNull UserInfo user, @NotNull UserStats statistics) {
        final List<IWebHit.Model> webHits = new ArrayList<IWebHit.Model>(solutions.getReferences().size());
        for (RestHit<SolutionReference> externalHit : solutions.getReferences()) {
            SolutionReference externalSolution = externalHit.getSolution();
            IMarkButton.Model mark = convertMarkPanel(externalHit);
            final String sourceIconName = externalSolution.getSource().getIcon();
            final URL sourceIconUrl = urlBuilder.sourceIcon(sourceIconName);

            String createdBy = null;
            if (externalSolution.getAuthor() != null) createdBy = externalSolution.getAuthor().getName();
            IWebHit.Model webHit = new IWebHit.Model(externalSolution.getTitle(), externalSolution.getUrl(), externalHit.getSolutionId(), externalSolution.getCreatedAt(), createdBy, externalSolution.getSource().getName(), sourceIconUrl, mark);
            webHits.add(webHit);
        }

        IWebResultsTab.Model webResults = new IWebResultsTab.Model(webHits);
        IHelpOthersCTA.Model cta = new IHelpOthersCTA.Model(0);
        final List<ITipHit.Model> tipHits = new ArrayList<ITipHit.Model>(solutions.getTips().size());
        for (RestHit<Tip> tipSolution : solutions.getTips()) {
            Tip tip = tipSolution.getSolution();
            IMarkButton.Model mark = convertMarkPanel(tipSolution);
            UserReference author = tipSolution.getCreatedBy();
            ITipHit.Model tipHit = new ITipHit.Model(tip.getTip(), tipSolution.getSolutionId(), tip.getCreatedAt(), author.getDisplayName(), author.getAvatarUrl(), mark);
            tipHits.add(tipHit);
        }
        final List<IBugmateHit.Model> bugmateHits = new ArrayList<IBugmateHit.Model>(bugmates.getBugmates().size());
        for (Bugmate b : bugmates.getBugmates()) {
            IBugmateHit.Model model = new IBugmateHit.Model(b.getUserId(), b.getDisplayName(), b.getAvatarUrl(), b.getNumberOfSearches(), b.getLastSeen());
            bugmateHits.add(model);
        }
        IBugmateList.Model bugmateList = new IBugmateList.Model(bugmateHits, bugmates.getNumberOfOtherBugmates(), bugmates.isEvenMoreExists());
        ITipResultsTab.Model tipResults = new ITipResultsTab.Model(tipHits, bugmateList);
        IResultTabs.Model resultTabs = new IResultTabs.Model(webResults, tipResults, cta);
        IExceptionHeaderPanel.Model header = new IExceptionHeaderPanel.Model(SolutionService.headLine(solutions.getSearchGroup().getLastSearch()));
        IProfilePanel.Model profile = new IProfilePanel.Model(0, statistics.getNumberOfMarks(), statistics.getNumberOfTips(), statistics.getNumberOfThanks(), user.getDisplayName(), user.getAvatarUrl());
        ISolutionFrame.Model model = new ISolutionFrame.Model(resultTabs, header, profile);

        return model;
    }

}

