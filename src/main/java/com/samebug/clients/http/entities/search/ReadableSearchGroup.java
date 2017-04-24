package com.samebug.clients.http.entities.search;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Date;

public final class ReadableSearchGroup extends SearchGroup {
    private Integer lastSearchId;
    private Date firstSeen;
    private Date lastSeen;
    private Integer numberOfSearches;
    private String helpRequestId;

    @NotNull
    public Integer getLastSearchId() {
        return lastSearchId;
    }

    @NotNull
    public Date getFirstSeen() {
        return firstSeen;
    }

    @NotNull
    public Date getLastSeen() {
        return lastSeen;
    }

    @NotNull
    public Integer getNumberOfSearches() {
        return numberOfSearches;
    }

    @Nullable
    public String getHelpRequestId() {
        return helpRequestId;
    }
}
