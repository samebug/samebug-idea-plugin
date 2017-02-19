package com.samebug.clients.common.ui.component.solutions;

import java.net.URL;
import java.util.Date;

public interface ITipHit {
    final class Model {
        public final String message;
        public final Date createdAt;
        public final String createdBy;
        public final URL createdByAvatarUrl;
        public final IMarkButton.Model mark;

        public Model(Model rhs) {
            this(rhs.message, rhs.createdAt, rhs.createdBy, rhs.createdByAvatarUrl, rhs.mark);
        }

        public Model(String message, Date createdAt, String createdBy, URL createdByAvatarUrl, IMarkButton.Model mark) {
            this.message = message;
            this.createdAt = createdAt;
            this.createdBy = createdBy;
            this.createdByAvatarUrl = createdByAvatarUrl;
            this.mark = mark;
        }
    }
}
