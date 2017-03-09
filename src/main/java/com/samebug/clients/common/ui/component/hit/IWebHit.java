/**
 * Copyright 2017 Samebug, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.samebug.clients.common.ui.component.hit;

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

    interface Listener {
        void urlClicked(URL url);
    }
}
