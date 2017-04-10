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
package com.samebug.clients.http.entities.profile;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.URL;

public final class LoggedInUser {
    @NotNull
    public final Integer userId;

    @NotNull
    public final String displayName;

    @Nullable
    public final URL avatarUrl;

    @NotNull
    public final Integer defaultWorkspaceId;

    @NotNull
    public final String apiKey;

    public LoggedInUser(@NotNull Integer userId, @NotNull String displayName, URL avatarUrl, @NotNull Integer defaultWorkspaceId, @NotNull String apiKey) {
        this.userId = userId;
        this.displayName = displayName;
        this.avatarUrl = avatarUrl;
        this.defaultWorkspaceId = defaultWorkspaceId;
        this.apiKey = apiKey;
    }
}
