/*
 * Copyright 2017 Samebug, Inc.
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
package com.samebug.clients.common.ui.component.bugmate;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.URL;
import java.util.Date;

public interface IBugmateHit {
    final class Model {
        @NotNull
        public final String displayName;
        @Nullable
        public final URL avatarUrl;
        @NotNull
        public final Integer nSeen;
        @NotNull
        public final Date lastSeen;
        public final ConnectionStatus status;

        public Model(Model rhs) {
            this(rhs.displayName, rhs.avatarUrl, rhs.nSeen, rhs.lastSeen, rhs.status);
        }

        public Model(@NotNull String displayName, @Nullable URL avatarUrl, @NotNull Integer nSeen, @NotNull Date lastSeen, @NotNull ConnectionStatus status) {
            this.displayName = displayName;
            this.avatarUrl = avatarUrl;
            this.nSeen = nSeen;
            this.lastSeen = lastSeen;
            this.status = status;
        }
    }

    interface Listener {
    }
}
