package com.samebug.clients.idea.messages.view;

import com.intellij.util.messages.Topic;

public interface HistoryViewListener {
    Topic<HistoryViewListener> TOPIC = Topic.create("history settings", HistoryViewListener.class);

    void setRecurringFilter(boolean on);
    void setZeroSolutionFilter(boolean on);
    void reload();
}
