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
package com.samebug.clients.idea.controllers;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.samebug.clients.http.exceptions.SamebugClientException;
import com.samebug.clients.idea.components.application.IdeaSamebugPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.Timer;
import java.util.TimerTask;

public class TimedTasks {
    static final Logger LOGGER = Logger.getInstance(TimedTasks.class);

    @NotNull
    final Timer timer;

    TimerTask userStatsRefresher;
    TimerTask webSocketChecker;

    public TimedTasks() {
        timer = new Timer("Samebug-timed-tasks");

        userStatsRefresher = new TimerTask() {
            @Override
            public void run() {
                reloadUserStats();
            }
        };
        webSocketChecker = new TimerTask() {
            @Override
            public void run() {
                checkWebSocketConnection();
            }
        };

        timer.schedule(userStatsRefresher, 5000, 60000);
        timer.schedule(webSocketChecker, 60000, 60000);
    }


    public void reloadUserStats() {
        final IdeaSamebugPlugin plugin = IdeaSamebugPlugin.getInstance();
        ApplicationManager.getApplication().executeOnPooledThread(new Runnable() {
            @Override
            public void run() {
                try {
                    plugin.profileService.loadUserStats();
                } catch (SamebugClientException e) {
                    LOGGER.warn("Failed to execute loadUserStats", e);
                }
            }
        });
    }

    public void checkWebSocketConnection() {
        final IdeaSamebugPlugin plugin = IdeaSamebugPlugin.getInstance();
        plugin.clientService.getWsClient().checkConnectionAndConnectOnBackgroundThreadIfNecessary();
    }
}
