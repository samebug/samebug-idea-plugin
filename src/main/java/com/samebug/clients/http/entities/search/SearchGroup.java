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
package com.samebug.clients.http.entities.search;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Date;

public final class SearchGroup {
    private String id;

    private Date firstSeen;
    private Date lastSeen;
    private Integer numberOfSearches;
    private QueryInfo lastSearchInfo;
    private Integer lastSearchId;
    private String helpRequestId;

    @NotNull
    public String getId() {
        return id;
    }

    @NotNull
    public Date getFirstSeen() {
        return firstSeen;
    }

    @NotNull
    public Date getLastSeen() {
        return lastSeen;
    }

    @NotNull
    public Integer getNumberOfSearches() {
        return numberOfSearches;
    }

    @Nullable
    public QueryInfo getLastSearchInfo() {
        return lastSearchInfo;
    }

    @Nullable
    public Integer getLastSearchId() {
        return lastSearchId;
    }

    @Nullable
    public String getHelpRequestId() {
        return helpRequestId;
    }
}
