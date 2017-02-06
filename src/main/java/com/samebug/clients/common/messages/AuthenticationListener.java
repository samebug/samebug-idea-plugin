package com.samebug.clients.common.messages;

import com.intellij.util.messages.Topic;

public interface AuthenticationListener {
    Topic<AuthenticationListener> TOPIC = Topic.create("authentication change", AuthenticationListener.class);

    // TODO it should have the user (name, id, apikey) and the workspace (name, id, permissions) as parameters
    void success(String apiKey);
    void fail();
}
