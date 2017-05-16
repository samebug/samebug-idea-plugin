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
package com.samebug.clients.http.entities;

import com.samebug.clients.common.entities.search.ReadableSearchGroup;
import com.samebug.clients.http.entities.helprequest.HelpRequestMatch;
import com.samebug.clients.http.entities.search.SearchGroup;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class EntityUtils {
    @Nullable
    public static ReadableSearchGroup getReadableStackTraceSearchGroup(@NotNull HelpRequestMatch match) {
        SearchGroup requesterGroup = match.getHelpRequest().getSearchGroup();
        SearchGroup mySearchGroup = match.getMatchingGroup();

        if (requesterGroup.getLastSearchId() != null && requesterGroup.getLastSearchInfo() != null) {
            return new ReadableSearchGroup(requesterGroup.getLastSearchInfo(), requesterGroup.getLastSearchId());
        } else if (mySearchGroup.getLastSearchId() != null && mySearchGroup.getLastSearchInfo() != null) {
            return new ReadableSearchGroup(mySearchGroup.getLastSearchInfo(), mySearchGroup.getLastSearchId());
        } else {
            return null;
        }
    }

    private EntityUtils() {}
}
