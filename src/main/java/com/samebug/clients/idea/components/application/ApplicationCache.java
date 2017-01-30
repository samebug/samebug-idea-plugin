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
package com.samebug.clients.idea.components.application;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.util.messages.MessageBusConnection;
import com.samebug.clients.common.entities.user.Statistics;
import com.samebug.clients.common.entities.user.User;
import com.samebug.clients.common.search.api.entities.UserInfo;
import com.samebug.clients.common.search.api.entities.UserStats;
import com.samebug.clients.idea.messages.client.UserModelListener;
import com.samebug.clients.idea.messages.client.UserStatsListener;
import com.samebug.clients.idea.messages.controller.ProfileListener;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.atomic.AtomicReference;

public class ApplicationCache implements UserStatsListener, UserModelListener {

    @NotNull
    final MessageBusConnection connection;

    @NotNull
    final AtomicReference<User> user;

    @NotNull
    final AtomicReference<Statistics> statistics;

    public ApplicationCache(@NotNull MessageBusConnection connection) {
        this.connection = connection;
        connection.subscribe(UserStatsListener.TOPIC, this);
        connection.subscribe(UserModelListener.TOPIC, this);

        user = new AtomicReference<User>();
        statistics = new AtomicReference<Statistics>();
    }


    @Nullable
    public User getUser() {
        return user.get();
    }

    @Nullable
    public Statistics getStatistics() {
        return statistics.get();
    }


    @Override
    public void successLoadUserInfo(UserInfo result) {
        final User user;
        if (result.getUserExist()) {
            final Long lWorkspaceId = IdeaSamebugPlugin.getInstance().getState().workspaceId;
            final Integer workspaceId;
            if (lWorkspaceId == null) workspaceId = null;
            else workspaceId = lWorkspaceId.intValue();
            user = new User(result.getUserId(), result.getDisplayName(), result.getAvatarUrl(), workspaceId);
        } else {
            user = null;
        }
        this.user.set(user);
        ApplicationManager.getApplication().getMessageBus().syncPublisher(ProfileListener.TOPIC).profileChange(user, statistics.get());
    }

    @Override
    public void failLoadUserInfo(Exception e) {
        this.user.set(null);
        ApplicationManager.getApplication().getMessageBus().syncPublisher(ProfileListener.TOPIC).profileChange(null, statistics.get());
    }

    @Override
    public void finishLoadHistory() {

    }

    @Override
    public void successGetUserStats(UserStats userStats) {
        final Statistics statistics = new Statistics(userStats.getNumberOfTips(), userStats.getNumberOfMarks(), userStats.getNumberOfThanks());
        this.statistics.set(statistics);
        ApplicationManager.getApplication().getMessageBus().syncPublisher(ProfileListener.TOPIC).profileChange(user.get(), statistics);
    }

    @Override
    public void failGetUserStats(Exception e) {
        this.statistics.set(null);
        ApplicationManager.getApplication().getMessageBus().syncPublisher(ProfileListener.TOPIC).profileChange(user.get(), null);
    }
}
