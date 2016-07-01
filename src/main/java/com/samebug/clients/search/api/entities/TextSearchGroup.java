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
package com.samebug.clients.search.api.entities;

import org.jetbrains.annotations.NotNull;

import java.util.Date;

public final class TextSearchGroup extends SearchGroup {
    @NotNull
    public TextSearch lastSearch;

    @Override
    @NotNull
    public TextSearch getLastSearch() {
        return lastSearch;
    }

    public TextSearchGroup(@NotNull final TextSearchGroup rhs) {
        this.id = rhs.id;
        this.visitorId = rhs.visitorId;
        this.userId = rhs.userId;
        this.teamId = rhs.teamId;
        this.numberOfSearches = rhs.numberOfSearches;
        this.numberOfHits = rhs.numberOfHits;
        this.firstSeen = new Date(rhs.firstSeen.getTime());
        this.lastSeen = new Date(rhs.lastSeen.getTime());
        this.lastSearch = new TextSearch(rhs.lastSearch);
    }

}
