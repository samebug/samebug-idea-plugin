package com.samebug.clients.http.entities.search;

import org.jetbrains.annotations.NotNull;

public final class TextSearchInfo extends QueryInfo {
    private String query;

    @NotNull
    public String getQuery() {
        return query;
    }
}
