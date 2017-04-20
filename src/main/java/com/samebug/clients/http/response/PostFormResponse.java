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
