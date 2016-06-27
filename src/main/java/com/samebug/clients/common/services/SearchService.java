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
package com.samebug.clients.common.services;

import com.samebug.clients.search.api.entities.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

final public class SearchService {
    @Nullable
    Solutions search;

    final int searchId;

    public SearchService(final int searchId) {
        this.searchId = searchId;
    }

    public void setSolutions(@Nullable final Solutions solutions) {
        search = solutions;
    }

    @Nullable
    public Solutions getSolutions() {
        return search;
    }

    public static boolean createdByUser(final int userId, @NotNull final RestHit hit) {
        assert hit.createdBy != null;
        return hit.createdBy.id.equals(userId);
    }

    public static boolean canBeMarked(final int userId, @NotNull final SearchGroup searchGroup, @NotNull final RestHit hit) {
        return hit.createdBy == null
                || !hit.createdBy.id.equals(userId)
                || !(searchGroup instanceof StackTraceSearchGroup)
                || !hit.stackTraceId.equals(((StackTraceSearchGroup) searchGroup).lastSearch.stackTrace.stackTraceId);
    }

    public static List<BreadCrumb> getMatchingBreadCrumb(@NotNull final SearchGroup search, @NotNull final RestHit hit) {
        if (search instanceof StackTraceSearchGroup) {
            return ((StackTraceSearchGroup) search).lastSearch.stackTrace.breadCrumbs.subList(0, hit.matchLevel);
        } else {
            return Collections.emptyList();
        }
    }
}
