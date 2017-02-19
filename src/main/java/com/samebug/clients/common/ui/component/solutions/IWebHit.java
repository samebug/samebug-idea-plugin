package com.samebug.clients.common.ui.component.solutions;

import org.jetbrains.annotations.Nullable;

import java.net.URL;
import java.util.Date;

public interface IWebHit {
    final class Model {
        public final String title;
        public final URL url;
        public final int solutionId;
        public final Date createdAt;
        public final String createdBy;
        @Nullable
        public final String sourceName;
        public final URL sourceIconUrl;
        public final IMarkButton.Model mark;

        public Model(Model rhs) {
            this(rhs.title, rhs.url, rhs.solutionId, rhs.createdAt, rhs.createdBy, rhs.sourceName, rhs.sourceIconUrl, rhs.mark);
        }

        public Model(String title, URL url, int solutionId, Date createdAt, String createdBy, @Nullable String sourceName, URL sourceIconUrl, IMarkButton.Model mark) {
            this.title = title;
            this.url = url;
            this.solutionId = solutionId;
            this.createdAt = createdAt;
            this.createdBy = createdBy;
            this.sourceName = sourceName;
            this.sourceIconUrl = sourceIconUrl;
            this.mark = mark;
        }
    }

}
