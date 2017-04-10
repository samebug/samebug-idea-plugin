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
package com.samebug.clients.http.entities.bugmate;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.URL;
import java.util.Date;

public class Bugmate {
    @NotNull
    private int userId;
    @Nullable
    private boolean online;
    @NotNull
    private String displayName;
    @NotNull
    private URL avatarUrl;
    @NotNull
    private Date lastSeen;

    public Bugmate(@NotNull int userId, boolean online, @NotNull String displayName, @NotNull URL avatarUrl, @NotNull Date lastSeen, @NotNull int numberOfSearches) {
        this.userId = userId;
        this.online = online;
        this.displayName = displayName;
        this.avatarUrl = avatarUrl;
        this.lastSeen = lastSeen;
        this.numberOfSearches = numberOfSearches;
    }

    @NotNull
    public int getUserId() {
        return userId;
    }

    @Nullable
    public boolean isOnline() {
        return online;
    }

    @NotNull
    public String getDisplayName() {
        return displayName;
    }

    @NotNull
    public URL getAvatarUrl() {
        return avatarUrl;
    }

    @NotNull
    public Date getLastSeen() {
        return lastSeen;
    }

    @NotNull
    public int getNumberOfSearches() {
        return numberOfSearches;
    }

    @NotNull
    private int numberOfSearches;
}
