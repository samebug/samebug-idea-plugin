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
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.samebug.clients.common.api.entities.UserInfo;
import com.samebug.clients.common.api.entities.UserStats;
import com.samebug.clients.common.api.entities.helpRequest.MatchingHelpRequest;
import com.samebug.clients.common.api.entities.solution.Solutions;
import com.samebug.clients.common.services.HelpRequestStore;
import com.samebug.clients.common.ui.frame.helpRequest.IHelpRequestFrame;
import com.samebug.clients.idea.components.application.IdeaSamebugPlugin;
import com.samebug.clients.idea.components.project.ToolWindowController;
import com.samebug.clients.idea.ui.controller.frame.BaseFrameController;
import com.samebug.clients.swing.ui.frame.helpRequest.HelpRequestFrame;

import java.util.concurrent.Future;

public final class HelpRequestController extends BaseFrameController<IHelpRequestFrame> implements Disposable {
    final static Logger LOGGER = Logger.getInstance(HelpRequestController.class);
    final String helpRequestId;

    final WriteTipListener writeTipListener;
    final HelpRequestFrameListener frameListener;

    final HelpRequestStore helpRequestStore;


    public HelpRequestController(ToolWindowController twc, Project project, final String helpRequestId) {
        super(twc, project, new HelpRequestFrame());
        this.helpRequestId = helpRequestId;

        IdeaSamebugPlugin plugin = IdeaSamebugPlugin.getInstance();
        helpRequestStore = plugin.helpRequestStore;
        frameListener = new HelpRequestFrameListener(this);
        writeTipListener = new WriteTipListener(this);
    }

    public String getHelpRequestId() {
        return helpRequestId;
    }


    public void load() {
        // TODO other controllers should also make sure to set the loading screen when starting to load content
        view.setLoading();
        MatchingHelpRequest helpRequest = helpRequestStore.getHelpRequest(helpRequestId);
        assert helpRequest != null;

        final Future<UserInfo> userInfoTask = concurrencyService.userInfo();
        final Future<UserStats> userStatsTask = concurrencyService.userStats();

        int accessibleSearchId = helpRequest.accessibleSearchInfo().id;

        final Future<Solutions> solutionsTask = concurrencyService.solutions(accessibleSearchId);
        final Future<MatchingHelpRequest> helpRequestTask = concurrencyService.helpRequest(helpRequestId);

        load(solutionsTask, helpRequestTask, userInfoTask, userStatsTask);
    }

    private void load(final Future<Solutions> solutionsTask,
                      final Future<MatchingHelpRequest> helpRequestTask,
                      final Future<UserInfo> userInfoTask,
                      final Future<UserStats> userStatsTask) {
        new LoadingTask() {
            @Override
            protected void load() throws Exception {
                final IHelpRequestFrame.Model model = conversionService.convertHelpRequestFrame(
                        solutionsTask.get(), helpRequestTask.get(), userInfoTask.get(), userStatsTask.get());
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
