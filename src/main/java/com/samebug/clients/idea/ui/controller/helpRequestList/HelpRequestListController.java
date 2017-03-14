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
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.samebug.clients.common.api.entities.UserInfo;
import com.samebug.clients.common.api.entities.UserStats;
import com.samebug.clients.common.api.entities.helpRequest.IncomingHelpRequests;
import com.samebug.clients.common.ui.frame.helpRequestList.IHelpRequestListFrame;
import com.samebug.clients.idea.components.project.ToolWindowController;
import com.samebug.clients.idea.ui.controller.frame.BaseFrameController;
import com.samebug.clients.swing.ui.frame.helpRequestList.HelpRequestListFrame;

import java.util.concurrent.Future;

public final class HelpRequestListController extends BaseFrameController<IHelpRequestListFrame> implements Disposable {
    final static Logger LOGGER = Logger.getInstance(HelpRequestListController.class);

    final HelpRequestListListener frameListener;
    final HelpRequestPreviewListener helpRequestPreviewListener;

    public HelpRequestListController(ToolWindowController twc, Project project) {
        super(twc, project, new HelpRequestListFrame());

        frameListener = new HelpRequestListListener(this);
        helpRequestPreviewListener = new HelpRequestPreviewListener(this);
    }

    public void load() {
        final Future<UserInfo> userInfoTask = concurrencyService.userInfo();
        final Future<UserStats> userStatsTask = concurrencyService.userStats();
        final Future<IncomingHelpRequests> helpRequestsTask = concurrencyService.incomingHelpRequests();

        load(helpRequestsTask, userInfoTask, userStatsTask);
    }

    private void load(final Future<IncomingHelpRequests> helpRequestsTask,
                      final Future<UserInfo> userInfoTask,
                      final Future<UserStats> userStatsTask) {
        new LoadingTask() {
            @Override
            protected void load() throws Exception {
                final IHelpRequestListFrame.Model model = conversionService.convertHelpRequestListFrame(
                        helpRequestsTask.get(), userInfoTask.get(), userStatsTask.get());
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
