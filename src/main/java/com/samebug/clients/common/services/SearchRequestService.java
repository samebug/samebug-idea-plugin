/**
 * Copyright 2017 Samebug, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.samebug.clients.common.services;

import com.samebug.clients.common.entities.search.RequestedSearch;
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
    public RequestedSearch searchStart(SearchInfo searchInfo, String stackTrace) {
        RequestedSearch request = new RequestedSearch(searchInfo, stackTrace);
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
        } else if (!(previousRequest instanceof RequestedSearch)) {
            // This search request seems to be in an illegal state, it should be a Requested.
            store.removeRequest(requestId);
            request = null;
        } else {
            // promote from Requested to Saved
            RequestedSearch requestedSearch = (RequestedSearch) previousRequest;
            request = new SavedSearch(searchInfo, requestedSearch.getTrace(), result);
            store.addRequest(request);
        }

        return request;
    }

    public void searchFailed(SearchInfo searchInfo) {
        store.removeRequest(searchInfo.requestId);
    }
}
