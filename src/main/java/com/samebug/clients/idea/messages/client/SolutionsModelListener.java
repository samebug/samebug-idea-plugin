package com.samebug.clients.idea.messages.client;

import com.intellij.util.messages.Topic;
import com.samebug.clients.search.api.entities.Solutions;

public interface SolutionsModelListener {
    Topic<SolutionsModelListener> TOPIC = Topic.create("solutions model changes", SolutionsModelListener.class);

    void start(int searchId);

    void success(int searchId, Solutions result);

    void fail(int searchId, java.lang.Exception e);

    void finish(int searchId);
}
