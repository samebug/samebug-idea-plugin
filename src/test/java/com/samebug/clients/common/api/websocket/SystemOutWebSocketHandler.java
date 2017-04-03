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

import io.netty.buffer.ByteBuf;

import java.nio.charset.Charset;

public class SystemOutWebSocketHandler implements WebSocketEventHandler {
    @Override
    public void connected() {
        System.out.println("Connected");
    }

    @Override
    public void text(String text) {
        System.out.println("Text received: " + text);

    }

    @Override
    public void binary(ByteBuf content) {
        System.out.println("Binary received: " + content.toString(Charset.forName("UTF-8")));
    }

    @Override
    public void closing(int statusCode, String reason) {
        System.out.println("Closing");

    }

    @Override
    public void disconnected() {
        System.out.println("Disconnected");
        System.exit(0);
    }

    @Override
    public void handshakeSucceeded() {
        System.out.println("Handshake succeeded");
    }
}
