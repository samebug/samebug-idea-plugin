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
package com.samebug.clients.common.services;

import com.intellij.util.messages.MessageBus;
import com.intellij.util.messages.Topic;
import com.samebug.clients.http.client.ConnectionService;
import com.samebug.clients.http.response.ConnectionStatus;

public final class IdeaConnectionService extends ConnectionService {
    final MessageBus messageBus;

    public IdeaConnectionService(MessageBus messageBus) {
        this.messageBus = messageBus;
    }

    @Override
    protected void startRequest() {
        messageBus.syncPublisher(ConnectionStatusListener.TOPIC).startRequest();
    }

    @Override
    protected void finishRequest(ConnectionStatus status) {
        messageBus.syncPublisher(ConnectionStatusListener.TOPIC).finishRequest(status);
    }

    @Override
    protected void connectionChange(boolean isConnected) {
        messageBus.syncPublisher(ConnectionStatusListener.TOPIC).connectionChange(isConnected);
    }

    @Override
    protected void authenticationChange(boolean isAuthenticated) {
        messageBus.syncPublisher(ConnectionStatusListener.TOPIC).authenticationChange(isAuthenticated);
    }

    @Override
    protected void apiToBeDeprecated() {
        messageBus.syncPublisher(ConnectionStatusListener.TOPIC).apiToBeDeprecated();
    }

    @Override
    protected void apiDeprecated() {
        messageBus.syncPublisher(ConnectionStatusListener.TOPIC).apiDeprecated();
    }

    public interface ConnectionStatusListener {
        Topic<ConnectionStatusListener> TOPIC = Topic.create("connection status change", ConnectionStatusListener.class);

        void startRequest();

        void finishRequest(ConnectionStatus status);

        /**
         * Called when the client becomes connected or disconnected
         * <p>
         * This is called only when there is a change.
         * Initially the client is supposed to be connected.
         *
         * @param isConnected current connection state
         */
        void connectionChange(boolean isConnected);

        /**
         * Called when the client becomes authenticated or unauthenticated
         * <p>
         * This is called only when there is a change.
         * Initially the client is supposed to be authenticated.
         *
         * @param isAuthenticated current authentication state
         */
        void authenticationChange(boolean isAuthenticated);

        /**
         * Called when the server sends to_be_deprecated status for the first time (resets when the service is configured)
         */
        void apiToBeDeprecated();

        /**
         * Called when the server sends deprecated status for the first time (resets when the service is configured)
         */
        void apiDeprecated();
    }

}
