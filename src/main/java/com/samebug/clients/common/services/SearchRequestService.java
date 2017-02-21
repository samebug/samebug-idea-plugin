package com.samebug.clients.common.services;

import com.samebug.clients.common.entities.search.Requested;
import com.samebug.clients.common.entities.search.SavedSearch;
import com.samebug.clients.common.entities.search.SearchInfo;
import com.samebug.clients.common.entities.search.SearchRequest;
import com.samebug.clients.common.search.api.entities.SearchResults;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public final class SearchRequestService {
    final SearchRequestStore store;

    public SearchRequestService(SearchRequestStore store) {
        this.store = store;
    }

    @NotNull
    public Requested searchStart(SearchInfo searchInfo, String stackTrace) {
        Requested request = new Requested(searchInfo, stackTrace);
        store.addRequest(request);
        return request;
    }

    @Nullable
    public SavedSearch searchSucceeded(SearchInfo searchInfo, SearchResults result) {
        UUID requestId = searchInfo.requestId;
        SearchRequest previousRequest = store.getRequest(requestId);
        SavedSearch request;
        if (previousRequest == null) {
            // We don't know this search. It succeeded, but was not requested. Just ignore it.
            request = null;
        } else if (!(previousRequest instanceof Requested)) {
            // This search request seems to be in an illegal state, it should be a Requested.
            store.removeRequest(requestId);
            request = null;
        } else {
            // promote from Requested to Saved
            Requested requested = (Requested) previousRequest;
            request = new SavedSearch(searchInfo, requested.getTrace(), result);
            store.addRequest(request);
        }

        return request;
    }

    public void searchFailed(SearchInfo searchInfo) {
        store.removeRequest(searchInfo.requestId);
    }
}
