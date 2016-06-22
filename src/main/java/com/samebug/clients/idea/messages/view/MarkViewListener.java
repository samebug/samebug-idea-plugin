package com.samebug.clients.idea.messages.view;

import com.intellij.util.messages.Topic;
import com.samebug.clients.idea.ui.component.organism.MarkPanel;

public interface MarkViewListener {
    Topic<MarkViewListener> TOPIC = Topic.create("mark view", MarkViewListener.class);

    void mark(int searchId, int solutionId, boolean up, MarkPanel markPanel);
}
