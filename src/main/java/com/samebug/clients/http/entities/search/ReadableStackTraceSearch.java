package com.samebug.clients.http.entities.search;

import com.samebug.clients.http.entities.user.SamebugUser;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Date;

public final class ReadableStackTraceSearch extends Search {
    private ReadableSearchGroup group;
    private Date timestamp;
    private String exceptionType;
    private String exceptionMessage;
    private String exceptionMessageSlug;
    private SamebugUser user;

    @NotNull
    public ReadableSearchGroup getGroup() {
        return group;
    }

    @NotNull
    public Date getTimestamp() {
        return timestamp;
    }

    @NotNull
    public String getExceptionType() {
        return exceptionType;
    }

    @Nullable
    public String getExceptionMessage() {
        return exceptionMessage;
    }

    @NotNull
    public String getExceptionMessageSlug() {
        return exceptionMessageSlug;
    }

    @NotNull
    public SamebugUser getUser() {
        return user;
    }
}
