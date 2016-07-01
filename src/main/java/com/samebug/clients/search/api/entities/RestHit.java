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
import org.jetbrains.annotations.Nullable;

public final class RestHit<T extends RestSolution> {
    @NotNull
    public Integer solutionId;
    @NotNull
    public T solution;
    @NotNull
    public String stackTraceId;
    @NotNull
    public Integer matchLevel;
    @NotNull
    public Integer score;
    @Nullable
    public Integer markId;
    @Nullable
    public UserReference createdBy;
    @Nullable
    public Exception exception;

    public RestHit(@NotNull final RestHit<T> rhs) {
        solutionId = rhs.solutionId;
        if (rhs.solution instanceof SolutionReference) {
            solution = (T) new SolutionReference((SolutionReference) rhs.solution);
        } else if (rhs.solution instanceof Tip) {
            solution = (T) new Tip((Tip) rhs.solution);
        }
        stackTraceId = rhs.stackTraceId;
        matchLevel = rhs.matchLevel;
        score = rhs.score;
        markId = rhs.markId;
        createdBy = rhs.createdBy == null ? null : new UserReference(rhs.createdBy);
        exception = rhs.exception == null ? null : new Exception(rhs.exception);
    }
}
