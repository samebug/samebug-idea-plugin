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
