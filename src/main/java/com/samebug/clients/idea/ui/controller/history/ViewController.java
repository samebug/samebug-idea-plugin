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

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.util.messages.MessageBusConnection;
import com.samebug.clients.idea.messages.RefreshTimestampsListener;
import org.jetbrains.annotations.NotNull;

final class ViewController implements RefreshTimestampsListener, HistoryCardListener {
    final static Logger LOGGER = Logger.getInstance(ModelController.class);
    @NotNull
    final HistoryFrameController controller;

    public ViewController(@NotNull final HistoryFrameController controller) {
        this.controller = controller;

        MessageBusConnection projectConnection = controller.myProject.getMessageBus().connect(controller);
        projectConnection.subscribe(RefreshTimestampsListener.TOPIC, this);
        projectConnection.subscribe(HistoryCardListener.TOPIC, this);
    }

    @Override
    public void refreshDateLabels() {
    }

    @Override
    public void titleClick(int searchId) {
        controller.twc.focusOnSearch(searchId);
    }
}
