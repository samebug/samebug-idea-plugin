package com.samebug.clients.http.websocket;

import com.samebug.clients.http.client.TestWithSamebugClient;
import com.samebug.clients.http.entities.notification.Notification;
import io.netty.buffer.ByteBuf;
import org.junit.Before;
import org.junit.Ignore;

@Ignore
public class WebSocketClientTest extends TestWithSamebugClient {
    boolean receivedSomeMessage;

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
        public void otherNotificationType(Notification notification) {
            System.err.println("Unhandled incoming notification: " + notification);
        }
    }
}
