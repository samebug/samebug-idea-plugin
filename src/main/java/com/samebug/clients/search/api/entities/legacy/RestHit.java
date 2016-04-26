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
package com.samebug.clients.search.api.entities.legacy;

import com.samebug.clients.search.api.entities.Exception;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RestHit<T extends RestSolution> {
    @NotNull
    public Integer solutionId;
    @NotNull
    public T solution;
    @NotNull
    public Integer stackId;
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
}
