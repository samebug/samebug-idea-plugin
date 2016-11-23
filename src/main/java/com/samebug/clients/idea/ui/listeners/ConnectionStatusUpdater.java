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
package com.samebug.clients.idea.ui.listeners;

import com.intellij.openapi.application.ApplicationManager;
import com.samebug.clients.common.search.api.client.ConnectionStatus;
import com.samebug.clients.idea.components.application.IdeaSamebugPlugin;
import com.samebug.clients.idea.messages.model.ConnectionStatusListener;
import com.samebug.clients.idea.ui.component.NetworkStatusIcon;
import org.jetbrains.annotations.NotNull;

final public class ConnectionStatusUpdater implements ConnectionStatusListener {
    final NetworkStatusIcon statusIcon;

    public ConnectionStatusUpdater(@NotNull NetworkStatusIcon statusIcon) {
        this.statusIcon = statusIcon;
    }

    @Override
    public void startRequest() {
        ApplicationManager.getApplication().invokeLater(new Runnable() {
            @Override
            public void run() {
                statusIcon.setStatusLoading();
            }
        });
    }

    @Override
    public void connectionChange(boolean isConnected) {

    }

    @Override
    public void authenticationChange(boolean isAuthenticated) {

    }

    @Override
    public void apiToBeDeprecated() {

    }

    @Override
    public void apiDeprecated() {

    }

    @Override
    public void finishRequest(final ConnectionStatus status) {
        if (status.attemptToConnect) {
            ApplicationManager.getApplication().invokeLater(new Runnable() {
                @Override
                public void run() {
                    if (IdeaSamebugPlugin.getInstance().getClient().getNumberOfActiveRequests() == 0) {
                        if (status.successfullyConnected) {
                            statusIcon.setStatusOk();
                        } else {
                            statusIcon.setStatusError();
                        }
                    }
                }
            });
        }
    }
}
