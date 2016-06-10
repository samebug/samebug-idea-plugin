package com.samebug.clients.idea.messages.view;

import com.intellij.util.messages.Topic;

public interface SearchViewListener {
    Topic<SearchViewListener> TOPIC = Topic.create("search view", SearchViewListener.class);

    void reload();
}
