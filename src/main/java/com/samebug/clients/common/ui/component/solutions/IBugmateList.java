package com.samebug.clients.common.ui.component.solutions;

import com.intellij.util.messages.Topic;
import com.samebug.clients.idea.ui.component.solutions.BugmateHit;

import java.util.List;

public interface IBugmateList {
    final class Model {
        public final List<BugmateHit.Model> bugmateHits;
        public final int numberOfOtherBugmates;
        public final boolean evenMoreExists;

        public Model(Model rhs) {
            this(rhs.bugmateHits, rhs.numberOfOtherBugmates, rhs.evenMoreExists);
        }

        public Model(List<BugmateHit.Model> bugmateHits, int numberOfOtherBugmates, boolean evenMoreExists) {
            this.bugmateHits = bugmateHits;
            this.numberOfOtherBugmates = numberOfOtherBugmates;
            this.evenMoreExists = evenMoreExists;
        }
    }

    interface Listener {
        Topic<Listener> TOPIC = Topic.create("IBugmateList", Listener.class);

        void askBugmates(IBugmateList source);
    }
}
