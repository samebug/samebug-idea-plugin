package com.samebug.clients.idea.messages;

import com.intellij.util.messages.Topic;

/**
 * Created by poroszd on 3/7/16.
 */
public interface HistoryListener {
    Topic<HistoryListener> UPDATE_HISTORY_TOPIC = Topic.create("update samebug history", HistoryListener.class);

    void reload();
    void toggleShowSearchedWithZeroSolution(boolean enabled);
    void toggleShowOldSearches(boolean enabled);
}
