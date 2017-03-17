package com.samebug.clients.idea.controllers;

import com.intellij.openapi.diagnostic.Logger;
import com.samebug.clients.common.api.client.Config;
import com.samebug.clients.common.api.entities.helpRequest.IncomingTip;
import com.samebug.clients.common.api.entities.helpRequest.MatchingHelpRequest;
import com.samebug.clients.common.api.websocket.NotificationHandler;
import com.samebug.clients.common.api.websocket.SamebugNotificationWatcher;
import com.samebug.clients.common.api.websocket.WebSocketClient;
import org.jetbrains.annotations.Nullable;

import javax.net.ssl.SSLException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

public final class WebSocketClientService {
    final private static Logger LOGGER = Logger.getInstance(WebSocketClientService.class);

    final NotificationController notificationController;
    @Nullable
    WebSocketClient client;

    public WebSocketClientService(NotificationController notificationController) {
        this.notificationController = notificationController;
    }

    public synchronized void configure(final Config config) {
        try {
            Map<String, Object> authHeaders = new HashMap<String, Object>();
            if (config.apiKey != null) authHeaders.put("X-Samebug-ApiKey", config.apiKey);
            if (config.workspaceId != null) authHeaders.put("X-Samebug-WorkspaceId", config.workspaceId);
            URI serverUri = URI.create(config.serverRoot);
            String host = serverUri.getHost();
            String scheme = serverUri.getScheme().endsWith("s") ? "wss://" : "ws://";
            URI endpointUri = URI.create(scheme + host + "/socket/notifications/websocket");
            this.client = new WebSocketClient(endpointUri, authHeaders, new SamebugNotificationWatcher(new NotificationHandler() {
                @Override
                public void helpRequestReceived(MatchingHelpRequest helpRequestNotification) {
                    notificationController.incomingHelpRequest(helpRequestNotification.helpRequest);
                }

                @Override
                public void tipReceived(IncomingTip tipNotification) {
                    // TODO
                }
            }));
        } catch (InterruptedException e) {
            LOGGER.warn("Failed to configure websocket client", e);
            client = null;
        } catch (SSLException e) {
            LOGGER.warn("Failed to configure websocket client", e);
            client = null;
        }
    }


}
