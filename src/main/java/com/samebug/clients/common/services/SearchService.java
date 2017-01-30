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
package com.samebug.clients.common.services;

import com.samebug.clients.common.search.api.entities.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

final public class SearchService {
    AtomicReference<Solutions> search;

    final int mySearchId;

    public SearchService(final int searchId) {
        this.search = new AtomicReference<Solutions>(null);
        this.mySearchId = searchId;
    }

    public void setSolutions(@Nullable final Solutions solutions) {
        this.search.set(solutions);
    }

    @Nullable
    public Solutions getSolutions() {
        return search.get();
    }

    @Nullable
    public RestHit marked(final int solutionId, @NotNull final MarkResponse mark) {
        @Nullable Solutions currentSearch = search.get();
        if (currentSearch == null) return null;
        else {
            Solutions updatedModel = currentSearch.asMarked(solutionId, mark);
            search.set(updatedModel);
            return updatedModel.getHitForSolutionId(solutionId);
        }
    }

    @Nullable
    public RestHit unmarked(final int solutionId, @NotNull final MarkResponse mark) {
        @Nullable Solutions currentModel = search.get();
        if (currentModel == null) return null;
        else {
            Solutions updatedModel = currentModel.asUnmarked(solutionId, mark);
            search.set(updatedModel);
            return updatedModel.getHitForSolutionId(solutionId);
        }
    }

    public void addTip(@NotNull final RestHit<Tip> tip) {
        @Nullable Solutions currentModel = search.get();
        if (currentModel != null) {
            Solutions updatedModel = currentModel.addTip(tip);
            search.set(updatedModel);
        }
    }

    @Nullable
    public RestHit getHit(@NotNull final Integer searchId, @NotNull final Integer solutionId) {
        @Nullable Solutions currentSearch = search.get();
        if (searchId.equals(mySearchId) && currentSearch != null) return currentSearch.getHitForSolutionId(solutionId);
        else return null;
    }

    @Nullable
    public RestHit getHitForVote(@NotNull final Integer voteId) {
        @Nullable Solutions currentSearch = search.get();
        if (currentSearch == null) return null;
        for (RestHit<SolutionReference> s : currentSearch.getReferences()) {
            if (voteId.equals(s.getMarkId())) return s;
        }
        for (RestHit<Tip> t : currentSearch.getTips()) {
            if (voteId.equals(t.getMarkId())) return t;
        }
        return null;
    }

    public static boolean createdByUser(final int userId, @NotNull final RestHit hit) {
        return hit.getCreatedBy().getId().equals(userId);
    }

    public static boolean canBeMarked(final int userId, @NotNull final SearchGroup searchGroup, @NotNull final RestHit hit) {
        return hit.getMarkId() == null
                && !(hit.getCreatedBy().getId().equals(userId)
                && (searchGroup instanceof StackTraceSearchGroup)
                && hit.getStackTraceId().equals(((StackTraceSearchGroup) searchGroup).getId()));
    }

    public static List<BreadCrumb> getMatchingBreadCrumb(@NotNull final SearchGroup search, @NotNull final RestHit hit) {
        if (search instanceof StackTraceSearchGroup) {
            return ((StackTraceSearchGroup) search).getLastSearch().getStackTrace().getBreadCrumbs().subList(0, hit.getMatchLevel());
        } else {
            return Collections.emptyList();
        }
    }
}
