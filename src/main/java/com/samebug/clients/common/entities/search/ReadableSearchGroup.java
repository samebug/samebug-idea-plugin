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
package com.samebug.clients.common.entities.search;

import com.samebug.clients.http.entities.search.QueryInfo;
import org.jetbrains.annotations.NotNull;

public final class ReadableSearchGroup {
    @NotNull
    private QueryInfo lastSearchInfo;
    @NotNull
    private Integer lastSearchId;

    public ReadableSearchGroup(@NotNull QueryInfo lastSearchInfo, @NotNull Integer lastSearchId) {
        this.lastSearchInfo = lastSearchInfo;
        this.lastSearchId = lastSearchId;
    }

    @NotNull
    public QueryInfo getLastSearchInfo() {
        return lastSearchInfo;
    }

    @NotNull
    public Integer getLastSearchId() {
        return lastSearchId;
    }
}
