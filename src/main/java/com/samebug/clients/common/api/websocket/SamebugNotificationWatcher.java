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
package com.samebug.clients.common.api.websocket;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.samebug.clients.common.api.entities.helpRequest.IncomingTip;
import com.samebug.clients.common.api.entities.helpRequest.MatchingHelpRequest;
import com.samebug.clients.common.api.json.Json;
import io.netty.buffer.ByteBuf;

public class SamebugNotificationWatcher implements WebSocketEventHandler {
    final static Gson gson = Json.gson;
    private final JsonParser parser;
    private NotificationHandler handler;

    public SamebugNotificationWatcher(NotificationHandler handler) {
        this.handler = handler;
        this.parser = new JsonParser();
    }

    @Override
    public void connected() {

    }

    @Override
    public void text(String message) {
        JsonObject obj = parser.parse(message).getAsJsonObject();
        JsonElement tip = obj.get("tip");
        if (tip == null) {
            handler.helpRequestReceived(gson.fromJson(obj, MatchingHelpRequest.class));
        } else {
            handler.tipReceived(gson.fromJson(obj, IncomingTip.class));
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
