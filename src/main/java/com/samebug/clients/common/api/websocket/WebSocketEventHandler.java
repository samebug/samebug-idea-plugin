package com.samebug.clients.common.api.websocket;

import io.netty.buffer.ByteBuf;

public interface WebSocketEventHandler {

    void connected();
    void text(String text);
    void binary(ByteBuf content);
    void closing(int statusCode, String reason);
    void disconnected();
    void handshakeSucceeded();
}
