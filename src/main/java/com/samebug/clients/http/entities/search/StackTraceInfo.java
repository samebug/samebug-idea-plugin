package com.samebug.clients.http.entities.search;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class StackTraceInfo extends QueryInfo {
    private String exceptionType;
    private String exceptionMessage;
    private String exceptionMessageSlug;

    @NotNull
    public String getExceptionType() {
        return exceptionType;
    }

    @Nullable
    public String getExceptionMessage() {
        return exceptionMessage;
    }

    @Nullable
    public String getExceptionMessageSlug() {
        return exceptionMessageSlug;
    }
}
