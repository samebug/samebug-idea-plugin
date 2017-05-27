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
package com.samebug.clients.http.entities.helprequest;

import com.samebug.clients.http.entities.search.SearchGroup;
import com.samebug.clients.http.entities.user.RegisteredSamebugUser;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Date;

public final class HelpRequest {
    private String id;
    private RegisteredSamebugUser requester;
    private Integer workspaceId;
    private Integer searchId;
    private SearchGroup searchGroup;
    private String context;
    private Date createdAt;
    private Date revokedAt;

    @NotNull
    public String getId() {
        return id;
    }

    @NotNull
    public RegisteredSamebugUser getRequester() {
        return requester;
    }

    @Nullable
    public Integer getWorkspaceId() {
        return workspaceId;
    }

    @NotNull
    public Integer getSearchId() {
        return searchId;
    }

    @NotNull
    public SearchGroup getSearchGroup() {
        return searchGroup;
    }

    @Nullable
    public String getContext() {
        return context;
    }

    @NotNull
    public Date getCreatedAt() {
        return createdAt;
    }

    @Nullable
    public Date getRevokedAt() {
        return revokedAt;
    }
}
