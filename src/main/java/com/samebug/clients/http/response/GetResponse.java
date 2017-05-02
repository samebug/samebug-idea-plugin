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
package com.samebug.clients.http.response;

import com.samebug.clients.http.exceptions.SamebugClientException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class GetResponse<Result> {
    @Nullable
    private final Result result;
    @Nullable
    private final SamebugClientException exception;
    @NotNull
    private final Type resultType;

    public GetResponse(@NotNull Result result) {
        this.result = result;
        this.exception = null;
        resultType = Type.SUCCESS;
    }

    public GetResponse(@NotNull SamebugClientException exception) {
        this.result = null;
        this.exception = exception;
        resultType = Type.EXCEPTION;
    }

    @NotNull
    public Result getResult() {
        assert result != null : "Cannot access this field when the response was of type " + resultType;
        return result;
    }

    @NotNull
    public SamebugClientException getException() {
        assert exception != null : "Cannot access this field when the response was of type " + resultType;
        return exception;
    }

    @NotNull
    public Type getResultType() {
        return resultType;
    }

    public enum Type {
        SUCCESS, EXCEPTION;
    }
}
