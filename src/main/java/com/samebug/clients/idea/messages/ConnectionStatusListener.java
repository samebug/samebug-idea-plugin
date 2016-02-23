package com.samebug.clients.idea.messages;

import com.intellij.util.messages.Topic;

/**
 * Created by poroszd on 2/23/16.
 */
public interface ConnectionStatusListener {
    Topic<ConnectionStatusListener> CONNECTION_STATUS_TOPIC = Topic.create("connection status change", ConnectionStatusListener.class);

    void startRequest();
    void finishRequest(boolean isConnected);
    void authorizationChange(boolean isAuthorized);
}
