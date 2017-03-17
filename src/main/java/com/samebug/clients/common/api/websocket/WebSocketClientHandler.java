package com.samebug.clients.common.api.websocket;

import com.intellij.openapi.diagnostic.Logger;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.websocketx.*;
import io.netty.util.CharsetUtil;

public class WebSocketClientHandler extends SimpleChannelInboundHandler<Object> {
    final private static Logger LOGGER = Logger.getInstance(WebSocketClientHandler.class);

    private final WebSocketClientHandshaker handshaker;
    private WebSocketEventHandler eventHandler;
    private ChannelPromise handshakeFuture;

    ChannelPromise getHandshakeFuture() {
        return handshakeFuture;
    }

    WebSocketClientHandler(WebSocketClientHandshaker handshaker, WebSocketEventHandler eventHandler) {
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
                    "Unexpected FullHttpResponse (status=" + response.status() +
                            ", content=" + response.content().toString(CharsetUtil.UTF_8) + ')');
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