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

import javax.swing.*;
import java.lang.Exception;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
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
        final Future<Solutions> solutionsTask = ApplicationManager.getApplication().executeOnPooledThread(new Callable<Solutions>() {
            @Override
            public Solutions call() throws Exception {
                return solutionService.loadSolutions(searchId);
            }
        });
        final Future<BugmatesResult> bugmatesTask = ApplicationManager.getApplication().executeOnPooledThread(new Callable<BugmatesResult>() {
            @Override
            public BugmatesResult call() throws Exception {
                return bugmateService.loadBugmates(searchId);
            }
        });

        ApplicationManager.getApplication().executeOnPooledThread(new Runnable() {
            @Override
            public void run() {
                try {
                    final Solutions solutions = solutionsTask.get();
                    final BugmatesResult bugmates = bugmatesTask.get();
                    final User user = profileStore.getUser();
                    final Statistics statistics = profileStore.getUserStats();
                    // TODO this is quite an edge case, when we could load the solutions but not the user, not sure how to handle it
                    if (user == null || statistics == null) throw new SamebugClientException("");
                    final SolutionFrame.Model model = convertSolutionFrame(solutions, bugmates, user, statistics);
                    ApplicationManager.getApplication().invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            view.setContent(model);
                        }
                    });

                } catch (SamebugClientException e) {
                    // TODO set error panel
                    ApplicationManager.getApplication().invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            view.setWarningLoading();
                        }
                    });
                } catch (InterruptedException e) {
                    // TODO set error panel
                    ApplicationManager.getApplication().invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            view.setWarningLoading();
                        }
                    });
                } catch (ExecutionException e) {
                    // TODO set error panel
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

    MarkPanel.Model convertMarkResponse(MarkResponse response) {
        return new MarkPanel.Model(response.getDocumentVotes(), response.getId(), true /*TODO*/);
    }

    MarkPanel.Model convertRetractedMarkResponse(MarkResponse response) {
        return new MarkPanel.Model(response.getDocumentVotes(), null, true /*TODO*/);
    }

    MarkPanel.Model convertMarkPanel(RestHit hit) {
        return new MarkPanel.Model(hit.getScore(), hit.getMarkId(), true /*TODO*/);
    }

    SolutionFrame.Model convertSolutionFrame(@NotNull Solutions solutions, @NotNull BugmatesResult bugmates, @NotNull User user, @NotNull Statistics statistics) {
        final List<WebHit.Model> webHits = new ArrayList<WebHit.Model>(solutions.getReferences().size());
        for (RestHit<SolutionReference> externalHit : solutions.getReferences()) {
            SolutionReference externalSolution = externalHit.getSolution();
            MarkPanel.Model mark = convertMarkPanel(externalHit);
            final String sourceIconName = externalSolution.getSource().getIcon();
            final URL sourceIconUrl = urlBuilder.sourceIcon(sourceIconName);

            String createdBy = null;
            if (externalSolution.getAuthor() != null) createdBy = externalSolution.getAuthor().getName();
            WebHit.Model webHit = new WebHit.Model(externalSolution.getTitle(), externalSolution.getUrl(), externalHit.getSolutionId(), externalSolution.getCreatedAt(), createdBy, externalSolution.getSource().getName(), sourceIconUrl, mark);
            webHits.add(webHit);
        }

        WebResultsTab.Model webResults = new WebResultsTab.Model(webHits);
        WriteTipCTA.Model cta = new WriteTipCTA.Model(0);
        final List<TipHit.Model> tipHits = new ArrayList<TipHit.Model>(solutions.getTips().size());
        for (RestHit<Tip> tipSolution : solutions.getTips()) {
            Tip tip = tipSolution.getSolution();
            MarkPanel.Model mark = convertMarkPanel(tipSolution);
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
        TipResultsTab.Model tipResults = new TipResultsTab.Model(tipHits, cta, bugmateList);
        ResultTabs.Model resultTabs = new ResultTabs.Model(webResults, tipResults);
        ExceptionHeaderPanel.Model header = new ExceptionHeaderPanel.Model(SolutionService.headLine(solutions.getSearchGroup().getLastSearch()));
        ProfilePanel.Model profile = new ProfilePanel.Model(0, statistics.getNumberOfMarks(), statistics.getNumberOfTips(), statistics.getNumberOfThanks(), user.getDisplayName(), user.getAvatarUrl());
        SolutionFrame.Model model = new SolutionFrame.Model(resultTabs, header, profile);

        return model;
    }

}

