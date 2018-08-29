/*
 * Copyright 2018 Samebug, Inc.
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
package com.samebug.clients.idea.components.application;

import com.google.gson.JsonParseException;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.samebug.clients.common.ui.component.profile.ConnectionStatus;
import com.samebug.clients.http.websocket.NotificationHandler;
import com.samebug.clients.http.websocket.SamebugWebSocketEventHandler;
import com.samebug.clients.idea.messages.WebSocketStatusUpdate;
import io.netty.buffer.ByteBuf;

public final class IdeaWebSocketEventHandler extends SamebugWebSocketEventHandler {
    private static final Logger LOGGER = Logger.getInstance(IdeaWebSocketEventHandler.class);

    public IdeaWebSocketEventHandler(NotificationHandler handler) {
        super(handler);
    }

    @Override
    public void connected() {
        LOGGER.trace("WS client connected");

        ApplicationManager.getApplication().getMessageBus().syncPublisher(WebSocketStatusUpdate.TOPIC).updateConnectionStatus(ConnectionStatus.ONLINE);
    }

    @Override
    public void text(String text) {
        LOGGER.trace("WS client received message:\n" + text);
        try {
            readMessage(text);
        } catch (JsonParseException e) {
            LOGGER.warn("Failed to parse ws message:\n" + text, e);
        }
    }

    @Override
    public void binary(ByteBuf content) {
        LOGGER.trace("WS client received binary message");
    }

    @Override
    public void closing(int statusCode, String reason) {
        LOGGER.trace("WS client is closing (" + statusCode + "): " + reason);
    }

    @Override
    public void disconnected() {
        LOGGER.trace("WS client disconnected");

        ApplicationManager.getApplication().getMessageBus().syncPublisher(WebSocketStatusUpdate.TOPIC).updateConnectionStatus(ConnectionStatus.OFFLINE);

        // IMPROVE try reconnect only after a few seconds
        IdeaSamebugPlugin.getInstance().clientService.getWsClient().checkConnectionAndConnectOnBackgroundThreadIfNecessary();
    }

    @Override
    public void handshakeSucceeded() {
        LOGGER.trace("WS client handshakeSucceeded");
    }
}
