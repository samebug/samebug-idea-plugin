package com.samebug.clients.idea.messages.view;

import com.intellij.util.messages.Topic;

public interface SolutionsViewListener {
    Topic<SolutionsViewListener> TOPIC = Topic.create("solutions view changes", SolutionsViewListener.class);
}
