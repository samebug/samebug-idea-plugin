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

import com.intellij.openapi.diagnostic.Logger;
import com.samebug.clients.idea.components.application.IdeaSamebugPlugin;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.websocketx.*;
import io.netty.util.CharsetUtil;

import java.text.MessageFormat;

public class WebSocketClientHandler extends SimpleChannelInboundHandler<Object> {
    final private static Logger LOGGER = Logger.getInstance(WebSocketClientHandler.class);

    private final WebSocketClientHandshaker handshaker;
    private WebSocketEventHandler eventHandler;
    private ChannelPromise handshakeFuture;

    ChannelPromise getHandshakeFuture() {
        return handshakeFuture;
    }

    WebSocketClientHandler(WebSocketClientHandshaker handshaker, WebSocketEventHandler eventHandler) {
        super(true);
        this.handshaker = handshaker;
        this.eventHandler = eventHandler;
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) {
        handshakeFuture = ctx.newPromise();
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        handshaker.handshake(ctx.channel());
        eventHandler.connected();
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        eventHandler.disconnected();
        // IMPROVE try reconnect only after a few seconds
        IdeaSamebugPlugin.getInstance().webSocketClientService.checkConnectionAndConnectOnBackgroundThreadIfNecessary();
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        Channel ch = ctx.channel();
        if (!handshaker.isHandshakeComplete()) {
            handshaker.finishHandshake(ch, (FullHttpResponse) msg);
            eventHandler.handshakeSucceeded();
            handshakeFuture.setSuccess();
            return;
        }

        if (msg instanceof FullHttpResponse) {
            FullHttpResponse response = (FullHttpResponse) msg;
            throw new IllegalStateException(
                    MessageFormat.format("Unexpected FullHttpResponse (status={0}, content={1})", response.status(), response.content().toString(CharsetUtil.UTF_8)));
        }

        WebSocketFrame frame = (WebSocketFrame) msg;
        if (frame instanceof TextWebSocketFrame) {
            eventHandler.text(((TextWebSocketFrame) frame).text());
        } else if (frame instanceof PongWebSocketFrame) {
            eventHandler.binary(frame.content());
        } else if (frame instanceof CloseWebSocketFrame) {
            eventHandler.closing(((CloseWebSocketFrame) frame).statusCode(), ((CloseWebSocketFrame) frame).reasonText());
            ch.close();
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        LOGGER.warn("Exception in websocket handler", cause);
        if (!handshakeFuture.isDone()) {
            handshakeFuture.setFailure(cause);
        }
        ctx.close();
    }
}
