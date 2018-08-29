/*
 * Copyright 2018 Samebug, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 *    http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.samebug.clients.common.ui.component.helpRequest;

import org.jetbrains.annotations.Nullable;

import java.net.URL;
import java.util.Date;

public interface IHelpRequestPreview {

    final class Model {
        public final String displayName;
        public final URL avatarUrl;
        public final Date createdAt;
        @Nullable
        public final Date viewedAt;
        public final String helpRequestBody;
        public final String helpRequestId;
        public final String exceptionBody;

        public Model(Model rhs) {
            this(rhs.displayName, rhs.avatarUrl, rhs.createdAt, rhs.viewedAt, rhs.helpRequestBody, rhs.helpRequestId, rhs.exceptionBody);
        }

        public Model(String displayName, URL avatarUrl, Date createdAt, @Nullable Date viewedAt, String helpRequestBody, String helpRequestId, String exceptionBody) {
            this.displayName = displayName;
            this.avatarUrl = avatarUrl;
            this.createdAt = createdAt;
            this.viewedAt = viewedAt;
            this.helpRequestBody = helpRequestBody;
            this.helpRequestId = helpRequestId;
            this.exceptionBody = exceptionBody;
        }
    }

    interface Listener {
        void previewClicked(IHelpRequestPreview source, String helpRequestId);
    }
}
