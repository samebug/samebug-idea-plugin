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
package com.samebug.clients.common.entities.search;

import com.samebug.clients.http.entities.jsonapi.CreatedSearchResource;
import com.samebug.clients.http.entities.jsonapi.NewSearchMeta;
import org.jetbrains.annotations.NotNull;

public class SavedSearch implements SearchRequest {
    @NotNull
    private final String trace;
    @NotNull
    private final SearchInfo searchInfo;
    @NotNull
    private final CreatedSearchResource savedSearch;

    public SavedSearch(@NotNull final SearchInfo searchInfo, @NotNull final String trace, @NotNull final CreatedSearchResource savedSearch) {
        this.searchInfo = searchInfo;
        this.savedSearch = savedSearch;
        final NewSearchMeta meta = savedSearch.getMeta();
        Integer lineOffset = meta.getFirstLine();
        int startOffset = 0;
        for (int line = 0; line < lineOffset; ++line) {
            startOffset = trace.indexOf("\n", startOffset) + 1;
        }
        this.trace = trace.substring(startOffset);
    }

    @Override
    @NotNull
    public String getTrace() {
        return trace;
    }

    @Override
    @NotNull
    public SearchInfo getSearchInfo() {
        return searchInfo;
    }

    @NotNull
    public Integer getSearchId() {
        return savedSearch.getData().getId();
    }

    public boolean hasTip() {
        return savedSearch.getMeta().getHasTips();
    }

    public boolean hasHelpRequest() {
        return savedSearch.getMeta().getHasHelpRequests();
    }

    public boolean hasBugmate() {
        return savedSearch.getMeta().getHasBugmates();
    }

    public boolean hasWebHit() {
        return savedSearch.getMeta().getHasExternalSolutions();
    }

    public SolutionType getSolutionType() {
        if (hasTip()) return SolutionType.TIP;
        else if (hasHelpRequest()) return SolutionType.HELP_REQUEST;
        else if (hasBugmate()) return SolutionType.BUGMATE;
        else if (hasWebHit()) return SolutionType.WEBHIT;
        return SolutionType.NONE;
    }

    public enum SolutionType {
        TIP, HELP_REQUEST, BUGMATE, WEBHIT, NONE
    }
}
