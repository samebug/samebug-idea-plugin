package com.samebug.clients.idea.messages.controller;

import com.intellij.util.messages.Topic;

public interface CloseListener {
    Topic<CloseListener> TOPIC = Topic.create("tool window focus", CloseListener.class);

    void closeSearchTab(int searchId);
}
