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
package com.samebug.clients.common.api.entities.helpRequest;

import com.samebug.clients.common.api.entities.search.SearchInfo;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Date;
import java.util.List;

public final class Group {
    @NotNull
    public final String id;
    @Nullable
    public final SearchInfo lastSearchInfo;
    @NotNull
    public final Integer numberOfSearches;
    @NotNull
    public final Date firstSeen;
    @NotNull
    public final Date lastSeen;
    @NotNull
    public final String helpRequestId;
    // TODO enum
    @NotNull
    public final String visibility;
    // TODO enum
    @NotNull
    public final List<String> permissions;

    public Group(@NotNull String id,
                 @NotNull Integer lastSearchId,
                 @Nullable SearchInfo lastSearchInfo,
                 @NotNull Integer numberOfSearches,
                 @NotNull Date firstSeen,
                 @NotNull Date lastSeen,
                 @NotNull String helpRequestId,
                 @NotNull String visibility,
                 @NotNull List<String> permissions) {
        this.id = id;
        this.lastSearchInfo = lastSearchInfo;
        this.numberOfSearches = numberOfSearches;
        this.firstSeen = firstSeen;
        this.lastSeen = lastSeen;
        this.helpRequestId = helpRequestId;
        this.visibility = visibility;
        this.permissions = permissions;
    }
}
