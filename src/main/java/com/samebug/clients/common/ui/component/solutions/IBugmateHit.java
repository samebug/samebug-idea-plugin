package com.samebug.clients.common.ui.component.solutions;

import com.intellij.util.messages.Topic;

import java.net.URL;
import java.util.Date;

public interface IBugmateHit {
    final class Model {
        public final int userId;
        public final String displayName;
        public final URL avatarUrl;
        public final int nSeen;
        public final Date lastSeen;

        public Model(Model rhs) {
            this(rhs.userId, rhs.displayName, rhs.avatarUrl, rhs.nSeen, rhs.lastSeen);
        }

        public Model(int userId, String displayName, URL avatarUrl, int nSeen, Date lastSeen) {
            this.userId = userId;
            this.displayName = displayName;
            this.avatarUrl = avatarUrl;
            this.nSeen = nSeen;
            this.lastSeen = lastSeen;
        }
    }

    interface Listener {
        Topic<Listener> TOPIC = Topic.create("IBugmateHit", Listener.class);
    }
}
