package com.samebug.clients.idea.messages.model;

import com.intellij.util.messages.Topic;
import com.samebug.clients.search.api.entities.SearchResults;

public interface HistoryModelListener {
    Topic<HistoryModelListener> TOPIC = Topic.create("history settings", HistoryModelListener.class);

    void start();
    void success(SearchResults result);
    void fail(java.lang.Exception e);
    void finish();
}
