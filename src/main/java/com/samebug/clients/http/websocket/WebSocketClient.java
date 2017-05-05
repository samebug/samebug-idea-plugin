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

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketClientHandshaker;
import io.netty.handler.codec.http.websocketx.WebSocketClientHandshakerFactory;
import io.netty.handler.codec.http.websocketx.WebSocketVersion;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;

import javax.net.ssl.SSLException;
import java.io.Closeable;
import java.net.URI;
import java.net.URISyntaxException;


public final class WebSocketClient implements Closeable {
    private final int port;
    private final String host;
    private final URI wsEndpoint;
    private final DefaultHttpHeaders headers;
    private final WebSocketEventHandler eventHandler;
    private final EventLoopGroup group;
    private final SslContext sslContext;
    private final Channel channel;

    public WebSocketClient(WebSocketConfig config) throws URISyntaxException, SSLException, InterruptedException {
        final int port = config.serverUri.getPort();
        final String scheme = config.serverUri.getScheme().endsWith("s") ? "wss" : "ws";
        final boolean isWss = "wss".equalsIgnoreCase(scheme);

        this.headers = new DefaultHttpHeaders();
        if (config.apiKey != null) headers.add("X-Samebug-ApiKey", config.apiKey);
        if (config.workspaceId != null) headers.add("X-Samebug-WorkspaceId", config.workspaceId);

        this.port = port == -1 ? (isWss ? 443 : 80) : port;
        this.host = config.serverUri.getHost();
        this.wsEndpoint = new URI(scheme, null, host, port, "/notifications/websocket", null, null);
        this.eventHandler = config.eventHandler;
        this.group = config.group;
        this.sslContext = isWss ? SslContextBuilder.forClient().trustManager(InsecureTrustManagerFactory.INSTANCE).build() : null;
        // IMPROVE the constructor blocks the thread with networking!
        this.channel = connect();
    }

    private Channel connect() throws InterruptedException {
        WebSocketClientHandshaker handshaker = WebSocketClientHandshakerFactory.newHandshaker(wsEndpoint, WebSocketVersion.V13, null, false, headers);
        final WebSocketClientHandler clientHandler = new WebSocketClientHandler(handshaker, eventHandler);

        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(group)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) {
                        ChannelPipeline p = ch.pipeline();
                        if (sslContext != null) {
                            p.addLast(sslContext.newHandler(ch.alloc(), WebSocketClient.this.host, WebSocketClient.this.port));
                        }
                        p.addLast(
                                new HttpClientCodec(),
                                new HttpObjectAggregator(65536),
                                clientHandler
                        );
                    }
                });

        Channel channel = bootstrap.connect(host, port).sync().channel();
        clientHandler.getHandshakeFuture().sync();
        return channel;
    }

    @Override
    public void close() {
        channel.writeAndFlush(new CloseWebSocketFrame()).syncUninterruptibly();
        channel.closeFuture().syncUninterruptibly();
    }


    public boolean isOpen() {
        return channel.isOpen();
    }
}
