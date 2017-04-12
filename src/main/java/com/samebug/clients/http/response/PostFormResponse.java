package com.samebug.clients.http.response;

import com.samebug.clients.http.exceptions.SamebugClientException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class PostFormResponse<Result, FormError extends SamebugFormError> {
    @Nullable
    private final Result result;
    @Nullable
    private final FormError formError;
    @Nullable
    private final SamebugClientException exception;
    @NotNull
    private final Type resultType;

    public PostFormResponse(@NotNull Result result) {
        this.result = result;
        this.formError = null;
        this.exception = null;
        resultType = Type.SUCCESS;
    }

    public PostFormResponse(@NotNull FormError formError) {
        this.result = null;
        this.formError = formError;
        this.exception = null;
        resultType = Type.FORM_ERROR;
    }

    public PostFormResponse(@NotNull SamebugClientException exception) {
        this.result = null;
        this.formError = null;
        this.exception = exception;
        resultType = Type.EXCEPTION;
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
