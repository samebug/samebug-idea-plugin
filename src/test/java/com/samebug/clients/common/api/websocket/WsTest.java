package com.samebug.clients.common.api.websocket;

import com.samebug.clients.common.api.entities.helpRequest.IncomingTip;
import com.samebug.clients.common.api.entities.helpRequest.MatchingHelpRequest;

import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;


public class WsTest {

    public static void main(String[] args) throws IOException, InterruptedException {
        Map<String, Object> authHeaders = new HashMap<String, Object>();
        authHeaders.put("X-Samebug-ApiKey", "355c042b-c10b-11e5-a334-000d3a317492");
        authHeaders.put("X-Samebug-WorkspaceId", "2");
        URI endpointUri = URI.create("ws://localhost:9000/socket/notifications/websocket");
        WebSocketClient client = new WebSocketClient(endpointUri, authHeaders, new SamebugNotificationWatcher(new NotificationHandler() {
            @Override
            public void helpRequestReceived(MatchingHelpRequest helpRequestNotification) {
                System.out.println(helpRequestNotification);
            }

            @Override
            public void tipReceived(IncomingTip tipNotification) {
                System.out.println(tipNotification);
            }
        }));
    }

}
