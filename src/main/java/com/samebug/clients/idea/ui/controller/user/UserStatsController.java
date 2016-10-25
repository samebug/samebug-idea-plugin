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
package com.samebug.clients.idea.ui.controller.user;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.util.messages.MessageBusConnection;
import com.samebug.clients.idea.messages.client.UserStatsListener;
import com.samebug.clients.search.api.entities.UserStats;
import org.jetbrains.annotations.NotNull;

public class UserStatsController implements UserStatsListener {
    final static Logger LOGGER = Logger.getInstance(UserStatsController.class);
    @NotNull
    final UserController controller;

    public UserStatsController(@NotNull final UserController controller) {
        this.controller = controller;

        MessageBusConnection projectConnection = controller.myProject.getMessageBus().connect(controller);
        projectConnection.subscribe(UserStatsListener.TOPIC, this);
    }

    @Override
    public void successGetUserStats(int userId, int workspaceId, UserStats userStats) {
        LOGGER.warn("Refreshed user status to " + userStats);
    }

    @Override
    public void failGetUserStats(int userId, int workspaceId, Exception e) {

    }
}
