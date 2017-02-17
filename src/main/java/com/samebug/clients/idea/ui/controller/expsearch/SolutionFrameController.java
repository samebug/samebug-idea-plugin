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
package com.samebug.clients.idea.ui.controller.expsearch;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.samebug.clients.common.entities.user.Statistics;
import com.samebug.clients.common.entities.user.User;
import com.samebug.clients.common.search.api.WebUrlBuilder;
import com.samebug.clients.common.search.api.entities.*;
import com.samebug.clients.common.search.api.exceptions.SamebugClientException;
import com.samebug.clients.common.services.BugmateService;
import com.samebug.clients.common.services.ProfileStore;
import com.samebug.clients.common.services.SolutionService;
import com.samebug.clients.common.services.SolutionStore;
import com.samebug.clients.idea.components.application.IdeaSamebugPlugin;
import com.samebug.clients.idea.components.project.ToolWindowController;
import com.samebug.clients.idea.ui.component.profile.ProfilePanel;
import com.samebug.clients.idea.ui.component.solutions.*;
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

final public class SolutionFrameController implements Disposable {
    final static Logger LOGGER = Logger.getInstance(SolutionFrameController.class);
    final ToolWindowController twc;
    final Project myProject;
    final int searchId;

    final SolutionFrame view;

    final ViewController viewController;

    private final WebUrlBuilder urlBuilder;
    private final ProfileStore profileStore;
    private final SolutionStore solutionStore;
    private final SolutionService solutionService;
    private final BugmateService bugmateService;

    public SolutionFrameController(ToolWindowController twc, Project project, final int searchId) {
        this.twc = twc;
        this.myProject = project;
        this.searchId = searchId;

        view = new SolutionFrame(myProject.getMessageBus());

        viewController = new ViewController(this);

        IdeaSamebugPlugin plugin = IdeaSamebugPlugin.getInstance();
        urlBuilder = plugin.getUrlBuilder();
        solutionStore = IdeaSamebugPlugin.getInstance().getSolutionStore();
        profileStore = IdeaSamebugPlugin.getInstance().getProfileStore();
        solutionService = plugin.getSolutionService();
        bugmateService = plugin.getBugmateService();
    }

    public void init() {
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

        ApplicationManager.getApplication().executeOnPooledThread(new Runnable() {
            @Override
            public void run() {
                try {
                    try {
                        final Solutions solutions = solutionsTask.get();
                        final BugmatesResult bugmates = bugmatesTask.get();
                        final User user = profileStore.getUser();
                        final Statistics statistics = profileStore.getUserStats();
                        // TODO this is quite an edge case, when we could load the solutions but not the user, not sure how to handle it
                        if (user == null || statistics == null || solutions == null || bugmates == null) throw new IllegalStateException("");
                        final SolutionFrame.Model model = convertSolutionFrame(solutions, bugmates, user, statistics);
                        ApplicationManager.getApplication().invokeLater(new Runnable() {
                            @Override
                            public void run() {
                                view.setContent(model);
                            }
                        });
                    } catch (IllegalStateException e) {
                        // TODO generic error, probably safe to retry
                        ApplicationManager.getApplication().invokeLater(new Runnable() {
                            @Override
                            public void run() {
                                view.setWarningLoading();
                            }
                        });
                    } catch (InterruptedException e) {
                        // TODO generic error, probably safe to retry
                        ApplicationManager.getApplication().invokeLater(new Runnable() {
                            @Override
                            public void run() {
                                view.setWarningLoading();
                            }
                        });
                    } catch (ExecutionException e) {
                        if (e.getCause() instanceof SamebugClientException) throw (SamebugClientException) e.getCause();
                        else {
                            // TODO generic error, probably safe to retry
                            ApplicationManager.getApplication().invokeLater(new Runnable() {
                                @Override
                                public void run() {
                                    view.setWarningLoading();
                                }
                            });
                        }
                    }
                } catch (SamebugClientException e) {
                    // TODO error with loading, bad connection, bad apikey, server error, etc
                    ApplicationManager.getApplication().invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            view.setWarningLoading();
                        }
                    });
                }
            }
        });
    }

    @NotNull
    public JPanel getControlPanel() {
        return view;
    }

    @Override
    public void dispose() {

    }

    MarkButton.Model convertMarkResponse(MarkResponse response) {
        return new MarkButton.Model(response.getDocumentVotes(), response.getId(), true /*TODO*/);
    }

    MarkButton.Model convertRetractedMarkResponse(MarkResponse response) {
        return new MarkButton.Model(response.getDocumentVotes(), null, true /*TODO*/);
    }

    MarkButton.Model convertMarkPanel(RestHit hit) {
        return new MarkButton.Model(hit.getScore(), hit.getMarkId(), true /*TODO*/);
    }

    SolutionFrame.Model convertSolutionFrame(@NotNull Solutions solutions, @NotNull BugmatesResult bugmates, @NotNull User user, @NotNull Statistics statistics) {
        final List<WebHit.Model> webHits = new ArrayList<WebHit.Model>(solutions.getReferences().size());
        for (RestHit<SolutionReference> externalHit : solutions.getReferences()) {
            SolutionReference externalSolution = externalHit.getSolution();
            MarkButton.Model mark = convertMarkPanel(externalHit);
            final String sourceIconName = externalSolution.getSource().getIcon();
            final URL sourceIconUrl = urlBuilder.sourceIcon(sourceIconName);

            String createdBy = null;
            if (externalSolution.getAuthor() != null) createdBy = externalSolution.getAuthor().getName();
            WebHit.Model webHit = new WebHit.Model(externalSolution.getTitle(), externalSolution.getUrl(), externalHit.getSolutionId(), externalSolution.getCreatedAt(), createdBy, externalSolution.getSource().getName(), sourceIconUrl, mark);
            webHits.add(webHit);
        }

        WebResultsTab.Model webResults = new WebResultsTab.Model(webHits);
        HelpOthersCTA.Model cta = new HelpOthersCTA.Model(0);
        final List<TipHit.Model> tipHits = new ArrayList<TipHit.Model>(solutions.getTips().size());
        for (RestHit<Tip> tipSolution : solutions.getTips()) {
            Tip tip = tipSolution.getSolution();
            MarkButton.Model mark = convertMarkPanel(tipSolution);
            UserReference author = tipSolution.getCreatedBy();
            TipHit.Model tipHit = new TipHit.Model(tip.getTip(), tip.getCreatedAt(), author.getDisplayName(), author.getAvatarUrl(), mark);
            tipHits.add(tipHit);
        }
        final List<BugmateHit.Model> bugmateHits = new ArrayList<BugmateHit.Model>(bugmates.getBugmates().size());
        for (Bugmate b : bugmates.getBugmates()) {
            BugmateHit.Model model = new BugmateHit.Model(b.getUserId(), b.getDisplayName(), b.getAvatarUrl(), b.getNumberOfSearches(), b.getLastSeen());
            bugmateHits.add(model);
        }
        BugmateList.Model bugmateList = new BugmateList.Model(bugmateHits, bugmates.getNumberOfOtherBugmates(), bugmates.isEvenMoreExists());
        TipResultsTab.Model tipResults = new TipResultsTab.Model(tipHits, bugmateList);
        ResultTabs.Model resultTabs = new ResultTabs.Model(webResults, tipResults, cta);
        ExceptionHeaderPanel.Model header = new ExceptionHeaderPanel.Model(SolutionService.headLine(solutions.getSearchGroup().getLastSearch()));
        ProfilePanel.Model profile = new ProfilePanel.Model(0, statistics.getNumberOfMarks(), statistics.getNumberOfTips(), statistics.getNumberOfThanks(), user.getDisplayName(), user.getAvatarUrl());
        SolutionFrame.Model model = new SolutionFrame.Model(resultTabs, header, profile);

        return model;
    }

}

