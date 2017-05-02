/*
 * Copyright 2017 Samebug, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 *    http://www.apache.org/licenses/LICENSE-2.0
 * <p>
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
import com.samebug.clients.http.entities.response.SearchRequest;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
    public SavedSearch searchSucceeded(SearchInfo searchInfo, RequestedSearch requestedSearch, SearchRequest result) {
        assert store.getRequest(searchInfo.requestId) == requestedSearch : "Promoting an illegal RequestedSearch";
        // promote from Requested to Saved
        SavedSearch request = new SavedSearch(searchInfo, requestedSearch.getTrace(), result.getData(), result.getMeta());
        store.addRequest(request);

        return request;
    }

    public void searchFailed(SearchInfo searchInfo) {
        store.removeRequest(searchInfo.requestId);
    }
}
