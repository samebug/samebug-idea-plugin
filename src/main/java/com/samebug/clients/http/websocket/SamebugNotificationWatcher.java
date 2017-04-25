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
package com.samebug.clients.http.websocket;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.samebug.clients.http.entities.notification.IncomingAnswer;
import com.samebug.clients.http.entities.notification.IncomingHelpRequest;
import com.samebug.clients.http.entities.notification.Notification;
import com.samebug.clients.http.json.Json;
import io.netty.buffer.ByteBuf;

public class SamebugNotificationWatcher implements WebSocketEventHandler {
    final static Gson gson = Json.gson;
    private NotificationHandler handler;

    public SamebugNotificationWatcher(NotificationHandler handler) {
        this.handler = handler;
    }

    @Override
    public void connected() {

    }

    @Override
    public void text(String message) {
        try {
            Notification n = gson.fromJson(message, Notification.class);
            if (n instanceof IncomingHelpRequest) handler.helpRequestReceived((IncomingHelpRequest) n);
            else if (n instanceof IncomingAnswer) handler.tipReceived((IncomingAnswer) n);
            else {
                // TODO report unhandled notification type
            }
        } catch (JsonParseException e) {
            // TODO report parse error
        }
    }

    @Override
    public void binary(ByteBuf content) {

    }

    @Override
    public void closing(int statusCode, String reason) {

    }

    @Override
    public void disconnected() {

    }

    @Override
    public void handshakeSucceeded() {

    }


}
