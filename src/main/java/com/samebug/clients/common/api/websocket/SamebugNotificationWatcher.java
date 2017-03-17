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
