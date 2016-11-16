package com.samebug.clients.common.entities.search;

import com.samebug.clients.search.api.entities.SearchResults;
import org.jetbrains.annotations.NotNull;

public class Saved implements SearchRequest {
    private final String trace;
    private final SearchResults savedSearch;

    public Saved(String trace, SearchResults savedSearch) {
        this.trace = trace;
        this.savedSearch = savedSearch;
    }

    @Override
    @NotNull
    public String getTrace() {
        return trace;
    }

    @NotNull
    public SearchResults getSavedSearch() {
        return savedSearch;
    }
}
