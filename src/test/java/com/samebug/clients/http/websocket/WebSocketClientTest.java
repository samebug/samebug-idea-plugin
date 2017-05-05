package com.samebug.clients.http.websocket;

import com.samebug.clients.http.client.TestWithSamebugClient;
import com.samebug.clients.http.entities.helprequest.NewHelpRequest;
import com.samebug.clients.http.entities.notification.IncomingAnswer;
import com.samebug.clients.http.entities.notification.IncomingHelpRequest;
import com.samebug.clients.http.entities.notification.Notification;
import io.netty.buffer.ByteBuf;
import io.netty.channel.nio.NioEventLoopGroup;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.net.URI;
import java.util.concurrent.Executors;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

@Ignore
public class WebSocketClientTest extends TestWithSamebugClient {
    boolean receivedSomeMessage;

    @Test
    public void receiveNotificationOnHelpRequest() throws Exception {
        WebSocketConfig config = new WebSocketConfig(URI.create("http://localhost:9000"), "355be195-c10b-11e5-a334-000d3a317492", 1,
                new TestHandler(), new NioEventLoopGroup(1, Executors.newSingleThreadExecutor()));
        WebSocketClient wsClient = new WebSocketClient(config);
        authenticatedClient.createHelpRequest(5641, new NewHelpRequest(null));
        Thread.sleep(2500);
        assertThat(receivedSomeMessage, equalTo(true));
    }


    @Before
    public void clearState() {
        receivedSomeMessage = false;
    }

    private final class TestHandler extends SamebugWebSocketEventHandler {
        TestHandler() {
            super(new TestNotificationHandler());
        }

        @Override
        public void connected() {
            System.out.println("WS connected");
        }

        @Override
        public void text(String text) {
            System.out.println("Received:\n" + text);
            super.readMessage(text);
        }

        @Override
        public void binary(ByteBuf content) {
            System.out.println("Received binary");
        }

        @Override
        public void closing(int statusCode, String reason) {
            System.out.println("Closing (" + statusCode + "): " + reason);
        }

        @Override
        public void disconnected() {
            System.out.println("Disconnected");
        }

        @Override
        public void handshakeSucceeded() {
            System.out.println("Handshake succeeded");
        }
    }

    private final class TestNotificationHandler implements NotificationHandler {
        @Override
        public void helpRequestReceived(IncomingHelpRequest helpRequestNotification) {
            receivedSomeMessage = true;
        }

        @Override
        public void tipReceived(IncomingAnswer tipNotification) {
            receivedSomeMessage = true;
        }

        @Override

        public void otherNotificationType(Notification notification) {
            System.err.println("Unhandled incoming notification: " + notification);
        }
    }
}
