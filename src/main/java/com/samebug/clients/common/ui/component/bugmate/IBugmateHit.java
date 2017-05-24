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

import com.samebug.clients.common.entities.user.SamebugUser;
import org.jetbrains.annotations.NotNull;

import java.util.Date;

public interface IBugmateHit {
    final class Model {
        @NotNull
        public final SamebugUser.Base user;
        @NotNull
        public final Integer nSeen;
        @NotNull
        public final Date lastSeen;
        @NotNull
        public final Integer level;
        @NotNull
        public final String matchingGroupId;

        public Model(Model rhs) {
            this(rhs.user, rhs.nSeen, rhs.lastSeen, rhs.level, rhs.matchingGroupId);
        }

        public Model(@NotNull SamebugUser.Base user, @NotNull Integer nSeen, @NotNull Date lastSeen, @NotNull Integer level, @NotNull String matchingGroupId) {
            this.user = user;
            this.nSeen = nSeen;
            this.lastSeen = lastSeen;
            this.level = level;
            this.matchingGroupId = matchingGroupId;
        }
    }

    interface Listener {
    }
}
