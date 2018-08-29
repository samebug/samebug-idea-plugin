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

import java.net.URL;
import java.util.Date;

public interface IHelpRequest {
    final class Model {
        public final String displayName;
        public final URL avatarUrl;
        public final Date createdAt;
        public final String helpRequestBody;

        public Model(Model rhs) {
            this(rhs.displayName, rhs.avatarUrl, rhs.createdAt, rhs.helpRequestBody);
        }

        public Model(String displayName, URL avatarUrl, Date createdAt, String helpRequestBody) {
            this.displayName = displayName;
            this.avatarUrl = avatarUrl;
            this.createdAt = createdAt;
            this.helpRequestBody = helpRequestBody;
        }
    }
}
