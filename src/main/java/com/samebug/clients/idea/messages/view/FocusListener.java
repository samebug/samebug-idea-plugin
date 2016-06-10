package com.samebug.clients.idea.messages.view;

import com.intellij.util.messages.Topic;

public interface FocusListener {
    Topic<FocusListener> TOPIC = Topic.create("tool window focus", FocusListener.class);

    void focusOnHistory();

    void focusOnSearch(int searchId);
}
