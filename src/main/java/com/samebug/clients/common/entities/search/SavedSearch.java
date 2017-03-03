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
package com.samebug.clients.common.entities.search;

import com.samebug.clients.common.search.api.entities.SearchResults;
import org.jetbrains.annotations.NotNull;

public class SavedSearch implements SearchRequest {
    private final String trace;
    private final SearchInfo searchInfo;
    private final SearchResults savedSearch;

    public SavedSearch(SearchInfo searchInfo, String trace, SearchResults savedSearch) {
        this.searchInfo = searchInfo;
        this.savedSearch = savedSearch;
        Integer lineOffset = savedSearch.getFirstLine();
        if (lineOffset != null) {
            int startOffset = 0;
            for (int line = 0; line < lineOffset; ++line) {
                startOffset = trace.indexOf("\n", startOffset) + 1;
            }
            this.trace = trace.substring(startOffset);
        } else {
            this.trace = trace;
        }
    }

    @Override
    @NotNull
    public String getTrace() {
        return trace;
    }

    @Override
    public SearchInfo getSearchInfo() {
        return searchInfo;
    }

    @NotNull
    public SearchResults getSavedSearch() {
        return savedSearch;
    }
}
