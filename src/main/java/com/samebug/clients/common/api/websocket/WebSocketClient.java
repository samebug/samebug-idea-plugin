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

import com.intellij.openapi.diagnostic.Logger;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.websocketx.*;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;

import javax.net.ssl.SSLException;
import java.io.Closeable;
import java.io.IOException;
import java.net.URI;
import java.util.Map;


public final class WebSocketClient implements Closeable {
    final private static Logger LOGGER = Logger.getInstance(WebSocketClient.class);

    private final int port;
    private final String host;
    private final SslContext sslContext;
    private final URI uri;
    private final Map<String, Object> customHeaders;
    private final Channel channel;
    private final EventLoopGroup group;
    private boolean closed;

    public WebSocketClient(URI uri, Map<String, Object> customHeaders, WebSocketEventHandler eventHandler) throws SSLException, InterruptedException {
        this.uri = uri;
        this.customHeaders = customHeaders;
        String scheme = this.uri.getScheme() == null ? "ws" : uri.getScheme();
        final boolean isWss = "wss".equalsIgnoreCase(scheme);
        final boolean isWs = "ws".equalsIgnoreCase(scheme);
        if (!isWs && !isWss) throw new IllegalArgumentException("Only WS(S) is supported.");

        this.port = uri.getPort() == -1 ? (isWss ? 443 : 80) : uri.getPort();
        this.host = uri.getHost() == null ? "127.0.0.1" : uri.getHost();
        this.sslContext = isWs ? null : SslContextBuilder.forClient().trustManager(InsecureTrustManagerFactory.INSTANCE).build();
        this.group = new NioEventLoopGroup();
        this.channel = connect(eventHandler);
    }

    private Channel connect(WebSocketEventHandler eventHandler) throws InterruptedException {
        DefaultHttpHeaders headers = new DefaultHttpHeaders();
        for (Map.Entry<String, Object> h : customHeaders.entrySet()) {
            headers.add(h.getKey(), h.getValue());
        }
        WebSocketClientHandshaker handshaker = WebSocketClientHandshakerFactory.newHandshaker(uri, WebSocketVersion.V13, null, false, headers);
        final WebSocketClientHandler clientHandler = new WebSocketClientHandler(handshaker, eventHandler);

        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) {
                        ChannelPipeline p = ch.pipeline();
                        if (sslContext != null) {
                            p.addLast(
                                    sslContext.newHandler(ch.alloc(), WebSocketClient.this.host, WebSocketClient.this.port)
                            );
                        }
                        p.addLast(
                                new HttpClientCodec(),
                                new HttpObjectAggregator(65536),
                                clientHandler
                        );
                    }
                });

        Channel channel = bootstrap.connect(WebSocketClient.this.host, WebSocketClient.this.port).sync().channel();
        clientHandler.getHandshakeFuture().sync();
        return channel;
    }

    @Override
    public void close() throws IOException {
        this.closed = true;
        try {
            channel.writeAndFlush(new CloseWebSocketFrame()).sync();
        } catch (InterruptedException ie) {
            LOGGER.warn("Failed to flush websocket", ie);
        }
        try {
            channel.closeFuture().sync();
        } catch (InterruptedException ie) {
            LOGGER.warn("Failed to flush websocket", ie);
        }
        group.shutdownGracefully();
    }


    public ChannelFuture send(WebSocketFrame frame) {
        if (closed) {
            throw new IllegalStateException("Client is closed");
        }
        return channel.writeAndFlush(frame);
    }
}
