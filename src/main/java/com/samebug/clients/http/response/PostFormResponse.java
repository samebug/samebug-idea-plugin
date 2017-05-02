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

public final class PostFormResponse<Result, FormError> {
    @Nullable
    private final Result result;
    @Nullable
    private final FormError formError;
    @Nullable
    private final SamebugClientException exception;
    @NotNull
    private final Type resultType;

    public static <Result, FormError> PostFormResponse<Result, FormError> fromResult(@NotNull Result result) {
        return new PostFormResponse<Result, FormError>(result, null, null);
    }

    public static <Result, FormError> PostFormResponse<Result, FormError> fromFormError(@NotNull FormError formError) {
        return new PostFormResponse<Result, FormError>(null, formError, null);
    }

    public static <Result, FormError> PostFormResponse<Result, FormError> fromException(@NotNull SamebugClientException exception) {
        return new PostFormResponse<Result, FormError>(null, null, exception);
    }

    private PostFormResponse(@Nullable Result result, @Nullable FormError formError, @Nullable SamebugClientException exception) {
        this.result = result;
        this.formError = formError;
        this.exception = exception;
        if (result != null && formError == null && exception == null) resultType = Type.SUCCESS;
        else if (result == null && formError != null && exception == null) resultType = Type.FORM_ERROR;
        else if (result == null && formError == null && exception != null) resultType = Type.EXCEPTION;
        else throw new IllegalArgumentException();
    }

    @NotNull
    public Result getResult() {
        assert result != null : "Cannot access this field when the response was of type " + resultType;
        return result;
    }

    @NotNull
    public FormError getFormError() {
        assert formError != null : "Cannot access this field when the response was of type " + resultType;
        return formError;
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
        SUCCESS, FORM_ERROR, EXCEPTION;
    }
}
