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
package com.samebug.clients.http.websocket;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.samebug.clients.http.entities.notification.Notification;
import com.samebug.clients.http.json.Json;

public abstract class SamebugWebSocketEventHandler implements WebSocketEventHandler {
    private static final Gson gson = Json.gson;
    private NotificationHandler handler;

    public SamebugWebSocketEventHandler(NotificationHandler handler) {
        this.handler = handler;
    }

    protected void readMessage(String message) throws JsonParseException {
        Notification n = gson.fromJson(message, Notification.class);
        handler.otherNotificationType(n);
    }
}
