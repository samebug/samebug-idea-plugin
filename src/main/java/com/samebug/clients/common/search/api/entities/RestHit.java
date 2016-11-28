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

public final class RestHit<T extends RestSolution> {
    @NotNull
    private Integer solutionId;
    @NotNull
    private T solution;
    @NotNull
    private String stackTraceId;
    @NotNull
    private Integer matchLevel;
    @NotNull
    private Integer score;
    @Nullable
    private Integer markId;
    @Nullable
    private UserReference createdBy;
    @Nullable
    private Exception exception;

    private RestHit(@NotNull final RestHit<T> rhs) {
        solutionId = rhs.solutionId;
        solution = rhs.solution;
        stackTraceId = rhs.stackTraceId;
        matchLevel = rhs.matchLevel;
        score = rhs.score;
        markId = rhs.markId;
        createdBy = rhs.createdBy;
        exception = rhs.exception;
    }

    @NotNull
    public Integer getSolutionId() {
        return solutionId;
    }

    @NotNull
    public T getSolution() {
        return solution;
    }

    @NotNull
    public String getStackTraceId() {
        return stackTraceId;
    }

    @NotNull
    public Integer getMatchLevel() {
        return matchLevel;
    }

    @NotNull
    public Integer getScore() {
        return score;
    }

    @Nullable
    public Integer getMarkId() {
        return markId;
    }

    @Nullable
    public UserReference getCreatedBy() {
        return createdBy;
    }

    @Nullable
    public Exception getException() {
        return exception;
    }


    // TODO these should not be part of the entity?
    @NotNull
    public RestHit<T> asMarked() {
        RestHit<T> marked = new RestHit<T>(this);
        marked.score = marked.getScore() + 1;
        marked.markId = -1;
        return marked;
    }

    @NotNull
    public RestHit<T> asUnmarked() {
        RestHit<T> marked = new RestHit<T>(this);
        marked.score = marked.getScore() - 1;
        marked.markId = null;
        return marked;
    }

    @NotNull
    public RestHit<T> asMarked(@NotNull final MarkResponse mark) {
        RestHit<T> marked = new RestHit<T>(this);
        marked.score = mark.getDocumentVotes();
        marked.markId = mark.getId();
        return marked;
    }

    @NotNull
    public RestHit<T> asUnmarked(@NotNull final MarkResponse mark) {
        RestHit<T> marked = new RestHit<T>(this);
        marked.score = mark.getDocumentVotes();
        marked.markId = null;
        return marked;
    }
}
