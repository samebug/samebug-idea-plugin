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
package com.samebug.clients.common.entities.user;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.URL;

public class User {
    @NotNull
    Integer userId;
    @NotNull
    String displayName;
    @NotNull
    URL avatarUrl;
    @Nullable
    Integer workspaceId;

    @NotNull
    public Integer getUserId() {
        return userId;
    }

    @NotNull
    public String getDisplayName() {
        return displayName;
    }

    @NotNull
    public URL getAvatarUrl() {
        return avatarUrl;
    }

    @Nullable
    public Integer getWorkspaceId() {
        return workspaceId;
    }

    public User(@NotNull Integer userId, @NotNull String displayName, @NotNull URL avatarUrl, @Nullable Integer workspaceId) {
        this.userId = userId;
        this.displayName = displayName;
        this.avatarUrl = avatarUrl;
        this.workspaceId = workspaceId;
    }

    @Override
    public int hashCode() {
        int workspaceHash = workspaceId == null ? 0 : workspaceId.hashCode();
        return (((31 + userId.hashCode()) * 31 + displayName.hashCode()) * 31 + avatarUrl.hashCode()) * 31 + workspaceHash;
    }

    @Override
    public boolean equals(Object that) {
        if (this == that) return true;
        else if (!(that instanceof User)) return false;
        else {
            final User rhs = (User) that;
            return rhs.userId.equals(userId)
                    && rhs.displayName.equals(displayName)
                    && rhs.avatarUrl.equals(avatarUrl)
                    && ((rhs.workspaceId == null && workspaceId == null) || (rhs.workspaceId != null && rhs.workspaceId.equals(workspaceId)));
        }
    }
}
