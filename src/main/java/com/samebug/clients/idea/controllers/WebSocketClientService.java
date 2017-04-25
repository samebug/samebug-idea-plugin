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
package com.samebug.clients.idea.controllers;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.samebug.clients.http.client.Config;
import com.samebug.clients.http.entities.notification.IncomingAnswer;
import com.samebug.clients.http.entities.notification.IncomingHelpRequest;
import com.samebug.clients.http.websocket.NotificationHandler;
import com.samebug.clients.http.websocket.SamebugNotificationWatcher;
import com.samebug.clients.http.websocket.WebSocketClient;
import com.samebug.clients.http.websocket.WebSocketConfig;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.handler.ssl.SslContextBuilder;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.ide.PooledThreadExecutor;

import javax.net.ssl.SSLException;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public final class WebSocketClientService implements Disposable {
    final static Logger LOGGER = Logger.getInstance(WebSocketClientService.class);
    final static long MinimalConnectBackoff = 10000L;

    final NotificationController notificationController;
    final AtomicReference<WebSocketClient> client;
    final EventLoopGroup group;

    long timestampOfLastConnect;
    @Nullable
    WebSocketConfig wsConfig;

    public WebSocketClientService(NotificationController notificationController) {
        this.notificationController = notificationController;
        this.client = new AtomicReference<WebSocketClient>(null);
        this.group = new NioEventLoopGroup(1, PooledThreadExecutor.INSTANCE);
    }

    public void configure(final Config config) {
        try {
            Map<String, Object> authHeaders = new HashMap<String, Object>();
            if (config.apiKey != null) authHeaders.put("X-Samebug-ApiKey", config.apiKey);
            if (config.workspaceId != null) authHeaders.put("X-Samebug-WorkspaceId", config.workspaceId);
            URI serverUri = URI.create(config.serverRoot);
            String host = serverUri.getHost();
            int port = serverUri.getPort();
            String scheme = serverUri.getScheme().endsWith("s") ? "wss" : "ws";
            URI endpointUri = new URI(scheme, null, host, port, "/socket/notifications/websocket", null, null);
            final SamebugNotificationWatcher eventHandler = new SamebugNotificationWatcher(new NotificationHandler() {
                @Override
                public void helpRequestReceived(IncomingHelpRequest helpRequestNotification) {
                    notificationController.incomingHelpRequest(helpRequestNotification);
                }

                @Override
                public void tipReceived(IncomingAnswer tipNotification) {
                    notificationController.incomingTip(tipNotification);
                }
            });

            this.wsConfig = new WebSocketConfig(endpointUri, authHeaders, eventHandler, group);
        } catch (URISyntaxException e) {
            LOGGER.warn("Failed to configure websocket client", e);
            this.wsConfig = null;
        }
        checkConnectionAndConnectOnBackgroundThreadIfNecessary();
    }


    public void checkConnectionAndConnectOnBackgroundThreadIfNecessary() {
        WebSocketClient currentClient = client.get();

        if (currentClient != null && currentClient.isOpen()) return;
        if (wsConfig == null) return;
        if (System.currentTimeMillis() < timestampOfLastConnect + MinimalConnectBackoff) return;

        LOGGER.info("Connecting websocket client");
        ApplicationManager.getApplication().executeOnPooledThread(new Runnable() {
            @Override
            public void run() {
                try {
                    timestampOfLastConnect = System.currentTimeMillis();
                    WebSocketClient c = WebSocketClientFactory.create(wsConfig);
                    client.set(c);
                    LOGGER.info("Successfully connected websocket client");
                } catch (Exception e) {
                    client.set(null);
                    LOGGER.warn("Failed to connect websocket client", e);
                }
            }
        });
    }

    public boolean isConnected() {
        WebSocketClient currentClient = client.get();
        return currentClient != null && currentClient.isOpen();
    }

    @Override
    public void dispose() {
        WebSocketClient c = client.get();
        if (c != null) {
            try {
                c.close();
            } catch (IOException e) {
                LOGGER.warn("Error on closing websocket", e);
            }
        }
        if (wsConfig != null) {
            // TODO do we want to handle this future?
            wsConfig.group.shutdownGracefully();
        }
    }
}

final class WebSocketClientFactory {
    static WebSocketClient create(WebSocketConfig config) throws SSLException, InterruptedException {
        boolean canWeUseWebsocket;
        try {
            Class<?> resolveSslContextBuilder = SslContextBuilder.class;
            canWeUseWebsocket = true;
        } catch (Throwable e) {
            canWeUseWebsocket = false;
        }
        if (canWeUseWebsocket) {
            return new WebSocketClient(config);
        } else {
            WebSocketClientService.LOGGER.warn("This intellij version does not have a websocket-compatible netty version");
            return null;
        }
    }
}

