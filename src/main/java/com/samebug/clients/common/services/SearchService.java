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
import java.util.concurrent.atomic.AtomicReference;

final public class SearchService {
    AtomicReference<Solutions> search;

    final int mySearchId;

    public SearchService(final int searchId) {
        this.search = new AtomicReference<Solutions>(null);
        this.mySearchId = searchId;
    }

    public void setSolutions(@Nullable final Solutions solutions) {
        // TODO make entity classes immutable
        this.search.set(solutions);
    }

    @Nullable
    public Solutions getSolutions() {
        return search.get();
    }

    @Nullable
    public RestHit marked(final int solutionId, @NotNull final MarkResponse mark) {
        Solutions currentSearch = search.get();
        if (currentSearch == null) return null;
        for (RestHit<SolutionReference> s : currentSearch.references) {
            if (s.solutionId == solutionId) {
                s.markId = mark.id;
                s.score = mark.marks;
                return s;
            }
        }
        for (RestHit<Tip> t : currentSearch.tips) {
            if (t.solutionId == solutionId) {
                t.markId = mark.id;
                t.score = mark.marks;
                return t;
            }
        }
        return null;
    }

    // TODO this is the same as marked, but MarkResponse does not differentiate posting and retracting
    @Nullable
    public RestHit unmarked(final int solutionId, @NotNull final MarkResponse mark) {
        Solutions currentSearch = search.get();
        if (currentSearch == null) return null;
        for (RestHit<SolutionReference> s : currentSearch.references) {
            if (s.solutionId == solutionId) {
                s.markId = null;
                s.score = mark.marks;
                return s;
            }
        }
        for (RestHit<Tip> t : currentSearch.tips) {
            if (t.solutionId == solutionId) {
                t.markId = null;
                t.score = mark.marks;
                return t;
            }
        }
        return null;
    }

    public void addTip(@NotNull final RestHit<Tip> tip) {
        Solutions currentSearch = search.get();
        if (currentSearch == null) return;
        currentSearch.tips.add(0, tip);
    }

    @Nullable
    public RestHit getHit(@NotNull final Integer searchId, @NotNull final Integer solutionId) {
        Solutions currentSearch = search.get();
        if (searchId.equals(mySearchId) && currentSearch != null) {
            for (RestHit<SolutionReference> s : currentSearch.references) {
                if (solutionId.equals(s.solutionId)) return new RestHit<SolutionReference>(s);
            }
            for (RestHit<Tip> t : currentSearch.tips) {
                if (solutionId.equals(t.solutionId)) return new RestHit<Tip>(t);
            }
            return null;
        } else {
            return null;
        }
    }

    @Nullable
    public RestHit getHitForVote(@NotNull final Integer voteId) {
        Solutions currentSearch = search.get();
        if (currentSearch == null) return null;
        for (RestHit<SolutionReference> s : currentSearch.references) {
            if (voteId.equals(s.markId)) return s;
        }
        for (RestHit<Tip> t : currentSearch.tips) {
            if (voteId.equals(t.markId)) return t;
        }
        return null;
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
