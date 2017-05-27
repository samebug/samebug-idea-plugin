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
package com.samebug.clients.idea.ui.controller.frame;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.util.messages.MessageBus;
import com.intellij.util.messages.MessageBusConnection;
import com.samebug.clients.common.ui.frame.IFrame;
import com.samebug.clients.http.response.ConnectionStatus;
import com.samebug.clients.idea.components.application.IdeaConnectionService;
import com.samebug.clients.idea.components.application.IdeaSamebugPlugin;

public final class ConnectionStatusController implements IdeaConnectionService.ConnectionStatusListener, Disposable {
    final IFrame frame;
    private final MessageBusConnection projectConnection;

    public ConnectionStatusController(IFrame frame, MessageBus messageBus) {
        this.frame = frame;

        projectConnection = messageBus.connect(this);
        projectConnection.subscribe(IdeaConnectionService.ConnectionStatusListener.TOPIC, this);

        // initialize the error bars if necessary
        IdeaConnectionService connectionService = IdeaSamebugPlugin.getInstance().clientService.getConnectionService();
        if (!connectionService.isConnected()) connectionChange(false);
        else if (!connectionService.isAuthenticated()) authenticationChange(false);
    }

    @Override
    public void startRequest() {

    }

    @Override
    public void finishRequest(ConnectionStatus status) {

    }

    @Override
    public void connectionChange(final boolean isConnected) {
        ApplicationManager.getApplication().invokeLater(new Runnable() {
            @Override
            public void run() {
                if (!isConnected) frame.showNetworkError();
                else frame.hideNetworkError();
            }
        });
    }

    @Override
    public void authenticationChange(final boolean isAuthenticated) {
        final IdeaConnectionService connectionService = IdeaSamebugPlugin.getInstance().clientService.getConnectionService();
        // NOTE being unauthenticated is only a problem when we are at least connected to the server
        ApplicationManager.getApplication().invokeLater(new Runnable() {
            @Override
            public void run() {
                if (connectionService.isConnected()) {
                    if (!isAuthenticated) frame.showAuthenticationError();
                    else frame.hideAuthenticationError();
                }
            }
        });
    }

    @Override
    public void apiToBeDeprecated() {

    }

    @Override
    public void apiDeprecated() {

    }

    @Override
    public void dispose() {
        projectConnection.dispose();
    }
}
