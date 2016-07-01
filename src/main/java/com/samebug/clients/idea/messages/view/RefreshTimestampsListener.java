package com.samebug.clients.idea.messages.view;

import com.intellij.util.messages.Topic;

public interface RefreshTimestampsListener {
    Topic<RefreshTimestampsListener> TOPIC = Topic.create("refreshDateLabels timestamp labels", RefreshTimestampsListener.class);

    void refreshDateLabels();
}
