/**
 * Copyright 2016 Samebug, Inc.
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
package com.samebug.clients.common.search.api.entities;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Date;

public abstract class SearchGroup {
    @NotNull
    protected String id;
    @Nullable
    protected String visitorId;
    @Nullable
    protected String userId;
    @Nullable
    protected String teamId;
    @NotNull
    protected Integer numberOfSearches;
    @NotNull
    protected Integer numberOfHits;
    @NotNull
    protected Date firstSeen;
    @NotNull
    protected Date lastSeen;

    @NotNull
    abstract public Search getLastSearch();

    @NotNull
    public String getId() {
        return id;
    }

    @Nullable
    public String getVisitorId() {
        return visitorId;
    }

    @Nullable
    public String getUserId() {
        return userId;
    }

    @Nullable
    public String getTeamId() {
        return teamId;
    }

    @NotNull
    public Integer getNumberOfSearches() {
        return numberOfSearches;
    }

    @NotNull
    public Integer getNumberOfHits() {
        return numberOfHits;
    }

    @NotNull
    public Date getFirstSeen() {
        return firstSeen;
    }

    @NotNull
    public Date getLastSeen() {
        return lastSeen;
    }
}
