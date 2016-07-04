/**
 * Copyright 2016 Samebug, Inc.
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
package com.samebug.clients.search.api.entities;

import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.net.URL;

public final class UserReference {
    @NotNull
    private Integer id;
    @NotNull
    private String displayName;
    @Nullable
    private URL avatarUrl;

    @NotNull
    public Integer getId() {
        return id;
    }

    @NotNull
    public String getDisplayName() {
        return displayName;
    }

    @Nullable
    public URL getAvatarUrl() {
        return avatarUrl;
    }
}
