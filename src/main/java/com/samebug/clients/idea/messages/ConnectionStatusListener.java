package com.samebug.clients.idea.messages;

import com.intellij.util.messages.Topic;

/**
 * Created by poroszd on 2/23/16.
 */
public interface ConnectionStatusListener {
    Topic<ConnectionStatusListener> CONNECTION_STATUS_TOPIC = Topic.create("connection status change", ConnectionStatusListener.class);

    void connectionStatusChange(boolean isConnected);
    void apiKeyChange(String apiKey, boolean isValid);
}
