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

import com.samebug.clients.http.entities.mark.Mark;
import com.samebug.clients.http.entities.mark.Votes;
import com.samebug.clients.http.entities.solution.Document;
import com.samebug.clients.http.entities.solution.SolutionSlot;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class SearchHit<D extends Document> {
    private SolutionSlot<D> solution;
    private Boolean isMarkable;
    private Mark activeMark;
    private Votes votes;
    private HitScore score;

    @NotNull
    public SolutionSlot<D> getSolution() {
        return solution;
    }

    @NotNull
    public Boolean getMarkable() {
        return isMarkable;
    }

    @Nullable
    public Mark getActiveMark() {
        return activeMark;
    }

    @NotNull
    public Votes getVotes() {
        return votes;
    }

    @NotNull
    public HitScore getScore() {
        return score;
    }
}
