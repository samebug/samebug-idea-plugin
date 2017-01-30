/**
 * Copyright 2017 Samebug, Inc.
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
package com.samebug.clients.idea.ui.controller.history;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.util.messages.MessageBusConnection;
import com.samebug.clients.common.search.api.exceptions.SamebugClientException;
import com.samebug.clients.idea.components.application.ClientService;
import com.samebug.clients.idea.components.application.IdeaSamebugPlugin;
import com.samebug.clients.idea.messages.view.HistoryViewListener;
import com.samebug.clients.idea.messages.view.RefreshTimestampsListener;
import org.jetbrains.annotations.NotNull;

final class ViewController implements HistoryViewListener, RefreshTimestampsListener {
    final static Logger LOGGER = Logger.getInstance(ModelController.class);
    @NotNull
    final HistoryTabController controller;

    public ViewController(@NotNull final HistoryTabController controller) {
        this.controller = controller;

        MessageBusConnection projectConnection = controller.myProject.getMessageBus().connect(controller);
        projectConnection.subscribe(HistoryViewListener.TOPIC, this);
        projectConnection.subscribe(RefreshTimestampsListener.TOPIC, this);
    }

    @Override
    public void setZeroSolutionFilter(boolean showZeroSolutionSearches) {
        controller.service.setShowZeroSolutionSearches(showZeroSolutionSearches);
        controller.refreshTab();
    }

    @Override
    public void setRecurringFilter(boolean showRecurringSearches) {
        controller.service.setShowRecurringSearches(showRecurringSearches);
        controller.refreshTab();
    }

    @Override
    public void reloadHistory() {
        ApplicationManager.getApplication().executeOnPooledThread(new Runnable() {
            @Override
            public void run() {
                ClientService client = IdeaSamebugPlugin.getInstance().getClient();
                try {
                    client.getSearchHistory();
                } catch (SamebugClientException e1) {
                    LOGGER.warn("Failed to download search history", e1);
                }
            }
        });
    }

    @Override
    public void refreshDateLabels() {
        controller.view.refreshDateLabels();
    }
}
