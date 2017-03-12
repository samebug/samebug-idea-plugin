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
package com.samebug.clients.common.api.entities.solution;

import com.samebug.clients.common.api.entities.Exception;
import com.samebug.clients.common.api.entities.UserReference;
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
    @NotNull
    private UserReference createdBy;
    @NotNull
    private Exception exception;
    @Nullable
    private Boolean tagged;

    private RestHit(@NotNull final RestHit<T> rhs) {
        solutionId = rhs.solutionId;
        solution = rhs.solution;
        stackTraceId = rhs.stackTraceId;
        matchLevel = rhs.matchLevel;
        score = rhs.score;
        markId = rhs.markId;
        createdBy = rhs.createdBy;
        exception = rhs.exception;
        tagged = rhs.tagged;
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

    @NotNull
    public UserReference getCreatedBy() {
        return createdBy;
    }

    @NotNull
    public Exception getException() {
        return exception;
    }

    @Nullable
    public Boolean getTagged() {
        return tagged;
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
