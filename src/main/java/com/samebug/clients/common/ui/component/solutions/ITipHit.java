package com.samebug.clients.common.ui.component.solutions;

import com.intellij.util.messages.Topic;

import java.net.URL;
import java.util.Date;

public interface ITipHit {
    final class Model {
        public final String message;
        public final int solutionId;
        public final Date createdAt;
        public final String createdBy;
        public final URL createdByAvatarUrl;
        public final IMarkButton.Model mark;

        public Model(Model rhs) {
            this(rhs.message, rhs.solutionId, rhs.createdAt, rhs.createdBy, rhs.createdByAvatarUrl, rhs.mark);
        }

        public Model(String message, int solutionId, Date createdAt, String createdBy, URL createdByAvatarUrl, IMarkButton.Model mark) {
            this.message = message;
            this.solutionId = solutionId;
            this.createdAt = createdAt;
            this.createdBy = createdBy;
            this.createdByAvatarUrl = createdByAvatarUrl;
            this.mark = mark;
        }
    }

    interface Listener {
        Topic<Listener> TOPIC = Topic.create("ITipHit", Listener.class);
    }
}
