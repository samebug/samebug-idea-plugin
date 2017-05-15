package com.samebug.clients.common.entities.search;

import com.samebug.clients.http.entities.search.QueryInfo;
import org.jetbrains.annotations.NotNull;

public final class ReadableSearchGroup {
    @NotNull
    private QueryInfo lastSearchInfo;
    @NotNull
    private Integer lastSearchId;

    public ReadableSearchGroup(@NotNull QueryInfo lastSearchInfo, @NotNull Integer lastSearchId) {
        this.lastSearchInfo = lastSearchInfo;
        this.lastSearchId = lastSearchId;
    }

    @NotNull
    public QueryInfo getLastSearchInfo() {
        return lastSearchInfo;
    }

    @NotNull
    public Integer getLastSearchId() {
        return lastSearchId;
    }
}
