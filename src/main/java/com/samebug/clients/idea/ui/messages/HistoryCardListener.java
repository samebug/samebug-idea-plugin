package com.samebug.clients.idea.ui.messages;

import com.intellij.util.messages.Topic;

public interface HistoryCardListener {
    Topic<HistoryCardListener> TOPIC = Topic.create("history card", HistoryCardListener.class);

    void titleClick(int searchId);

}
