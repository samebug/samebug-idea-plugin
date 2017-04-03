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
package com.samebug.clients.common.api.entities.helpRequest;

import com.samebug.clients.common.api.entities.search.SearchInfo;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Date;

public final class MatchingHelpRequest {
    @NotNull
    public final HelpRequest helpRequest;
    @NotNull
    public final Group matchingGroup;
    @NotNull
    public final Group requestGroup;
    @Nullable
    public final Date viewedAt;

    public MatchingHelpRequest(@NotNull HelpRequest helpRequest, @NotNull Group matchingGroup, @NotNull Group requestGroup,
                               @Nullable Date viewedAt) {
        this.helpRequest = helpRequest;
        this.matchingGroup = matchingGroup;
        this.requestGroup = requestGroup;
        this.viewedAt = viewedAt;
    }

    @NotNull
    public SearchInfo accessibleSearchInfo() {
        assert matchingGroup.lastSearchInfo != null : "you should always be able to access your own search";
        return requestGroup.lastSearchInfo != null ? requestGroup.lastSearchInfo : matchingGroup.lastSearchInfo;
    }
}
