package com.samebug.clients.idea.messages;

import com.intellij.util.messages.Topic;
import com.samebug.clients.common.ui.component.bugmate.ConnectionStatus;

public interface WebSocketStatusUpdate {
    Topic<WebSocketStatusUpdate> TOPIC = Topic.create("websocket status update", WebSocketStatusUpdate.class);

    void updateConnectionStatus(ConnectionStatus status);
}
