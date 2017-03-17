package com.samebug.clients.common.api.websocket;

import io.netty.buffer.ByteBuf;

import java.nio.charset.Charset;

public class SystemOutWebSocketHandler implements WebSocketEventHandler {
    @Override
    public void connected() {
        System.out.println("Connected" );
    }

    @Override
    public void text(String text) {
        System.out.println("Text received: " + text);

    }

    @Override
    public void binary(ByteBuf content) {
        System.out.println("Binary received: " + content.toString(Charset.forName("UTF-8" )));
    }

    @Override
    public void closing(int statusCode, String reason) {
        System.out.println("Closing" );

    }

    @Override
    public void disconnected() {
        System.out.println("Disconnected" );
        System.exit(0);
    }

    @Override
    public void handshakeSucceeded() {
        System.out.println("Handshake succeeded" );
    }
}
