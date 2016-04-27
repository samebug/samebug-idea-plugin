/**
 * Copyright 2016 Samebug, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.samebug.clients.idea.actions;

import com.intellij.ide.actions.RefreshAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.samebug.clients.idea.components.application.IdeaClientService;
import com.samebug.clients.idea.components.application.IdeaSamebugPlugin;
import com.samebug.clients.idea.ui.controller.HistoryTabController;
import com.samebug.clients.search.api.entities.GroupedHistory;
import com.samebug.clients.search.api.exceptions.SamebugClientException;

final public class ReloadHistoryAction extends RefreshAction implements DumbAware {
    @Override
    public void actionPerformed(final AnActionEvent e) {
        e.getPresentation().setEnabled(false);
        final Project project = e.getProject();
        if (project != null) {
            final HistoryTabController historyTab = ServiceManager.getService(e.getProject(), HistoryTabController.class);
            historyTab.update(null);

            ApplicationManager.getApplication().executeOnPooledThread(new Runnable() {
                @Override
                public void run() {
                    IdeaClientService client = IdeaSamebugPlugin.getInstance().getClient();
                    try {
                        final GroupedHistory history = client.getSearchHistory();
                        ApplicationManager.getApplication().invokeLater(new Runnable() {
                            @Override
                            public void run() {
                                historyTab.update(history);
                            }
                        });
                    } catch (SamebugClientException e1) {
                        LOGGER.warn("Failed to retrieve history", e1);
                    } finally {
                        e.getPresentation().setEnabled(true);
                    }
                }
            });
        }
    }

    @Override
    public void update(AnActionEvent e) {
        IdeaClientService client = IdeaSamebugPlugin.getInstance().getClient();
        e.getPresentation().setEnabled(client.getNumberOfActiveRequests() == 0);
    }

    final static Logger LOGGER = Logger.getInstance(ReloadHistoryAction.class);
}
