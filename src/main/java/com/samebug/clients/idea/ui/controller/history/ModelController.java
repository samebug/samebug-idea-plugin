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
package com.samebug.clients.idea.ui.controller.history;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.util.messages.MessageBusConnection;
import com.samebug.clients.common.messages.HistoryModelListener;
import com.samebug.clients.common.search.api.entities.SearchHistory;
import com.samebug.clients.idea.ui.component.tab.HistoryTabView;
import org.jetbrains.annotations.NotNull;

import java.util.List;

final class ModelController implements HistoryModelListener {
    final static Logger LOGGER = Logger.getInstance(ModelController.class);
    @NotNull
    final HistoryFrameController controller;

    public ModelController(@NotNull final HistoryFrameController controller) {
        this.controller = controller;

        MessageBusConnection projectConnection = controller.myProject.getMessageBus().connect(controller);
        projectConnection.subscribe(HistoryModelListener.TOPIC, this);
    }

    @Override
    public void startLoadHistory() {
        ApplicationManager.getApplication().invokeLater(new Runnable() {
            @Override
            public void run() {
                controller.view.setWarningLoading();
            }
        });
    }

    @Override
    public void successLoadHistory(final SearchHistory result) {
        final List<HistoryTabView.Card.Model> models = controller.convert(result);

        ApplicationManager.getApplication().invokeLater(new Runnable() {
            @Override
            public void run() {
                controller.view.update(models);
            }
        });
    }

    @Override
    public void failLoadHistory(Exception e) {
        ApplicationManager.getApplication().invokeLater(new Runnable() {
            @Override
            public void run() {
                controller.view.setWarningLoading();
            }
        });
    }

    @Override
    public void finishLoadHistory() {
    }
}
