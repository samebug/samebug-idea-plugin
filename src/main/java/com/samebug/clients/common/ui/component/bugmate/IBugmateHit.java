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
package com.samebug.clients.common.ui.component.bugmate;

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
    }
}