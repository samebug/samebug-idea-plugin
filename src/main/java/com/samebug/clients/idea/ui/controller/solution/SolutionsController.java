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
import com.samebug.clients.common.api.entities.UserInfo;
import com.samebug.clients.common.api.entities.UserStats;
import com.samebug.clients.common.api.entities.bugmate.BugmatesResult;
import com.samebug.clients.common.api.entities.search.SearchDetails;
import com.samebug.clients.common.api.entities.solution.Solutions;
import com.samebug.clients.common.ui.frame.solution.ISolutionFrame;
import com.samebug.clients.idea.ui.controller.toolwindow.ToolWindowController;
import com.samebug.clients.idea.ui.controller.component.ProfileController;
import com.samebug.clients.idea.ui.controller.frame.BaseFrameController;
import com.samebug.clients.swing.ui.frame.solution.SolutionFrame;

import java.util.concurrent.Future;

public final class SolutionsController extends BaseFrameController<ISolutionFrame> implements Disposable {
    final static Logger LOGGER = Logger.getInstance(SolutionsController.class);

    final int searchId;

    final ViewController viewController;
    final ExceptionHeaderController exceptionHeaderController;
    final WebResultsTabController webResultsTabController;
    final BugmateListController bugmateListController;
    final HelpOthersCTAController helpOthersCTAController;
    final WebHitController webHitController;
    final MarkController markController;
    final ProfileController profileController;
    final SolutionFrameController solutionFrameController;

    public SolutionsController(ToolWindowController twc, Project project, final int searchId) {
        super(twc, project, new SolutionFrame());
        this.searchId = searchId;

        viewController = new ViewController(this);
        exceptionHeaderController = new ExceptionHeaderController(this);
        webResultsTabController = new WebResultsTabController(this);
        bugmateListController = new BugmateListController(this);
        helpOthersCTAController = new HelpOthersCTAController(this);
        profileController = new ProfileController(this);
        markController = new MarkController(this);
        webHitController = new WebHitController(this);
        solutionFrameController = new SolutionFrameController(this);
    }

    public int getSearchId() {
        return searchId;
    }

    public void load() {
        final Future<UserInfo> userInfoTask = concurrencyService.userInfo();
        final Future<UserStats> userStatsTask = concurrencyService.userStats();
        final Future<Solutions> solutionsTask = concurrencyService.solutions(searchId);
        final Future<BugmatesResult> bugmatesTask = concurrencyService.bugmates(searchId);
        final Future<SearchDetails> searchTask = concurrencyService.search(searchId);
        load(solutionsTask, bugmatesTask, searchTask, userInfoTask, userStatsTask);
    }

    private void load(final Future<Solutions> solutionsTask,
                      final Future<BugmatesResult> bugmatesTask,
                      final Future<SearchDetails> searchTask,
                      final Future<UserInfo> userInfoTask,
                      final Future<UserStats> userStatsTask) {
        new LoadingTask() {
            @Override
            protected void load() throws Exception {
                final SolutionFrame.Model model = conversionService.solutionFrame(searchTask.get(), solutionsTask.get(), bugmatesTask.get(), userInfoTask.get(), userStatsTask.get());
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

