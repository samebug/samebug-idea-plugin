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
package com.samebug.clients.idea.ui.controller.history;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.util.messages.MessageBusConnection;
import com.samebug.clients.idea.messages.client.HistoryModelListener;
import com.samebug.clients.search.api.entities.SearchHistory;
import org.jetbrains.annotations.NotNull;

final class ModelController implements HistoryModelListener {
    final static Logger LOGGER = Logger.getInstance(ModelController.class);
    @NotNull
    final HistoryTabController controller;

    public ModelController(@NotNull final HistoryTabController controller) {
        this.controller = controller;

        MessageBusConnection projectConnection = controller.myProject.getMessageBus().connect(controller);
        projectConnection.subscribe(HistoryModelListener.TOPIC, this);
    }

    @Override
    public void start() {
        ApplicationManager.getApplication().invokeLater(new Runnable() {
            @Override
            public void run() {
                controller.view.setWarningLoading();
            }
        });
    }

    @Override
    public void success(final SearchHistory result) {
        controller.service.setHistory(result);
        ApplicationManager.getApplication().invokeLater(new Runnable() {
            @Override
            public void run() {
                controller.refreshTab();
            }
        });
    }

    @Override
    public void fail(Exception e) {
        controller.service.setHistory(null);
        ApplicationManager.getApplication().invokeLater(new Runnable() {
            @Override
            public void run() {
                controller.refreshTab();
            }
        });
    }

    @Override
    public void finish() {
    }
}
