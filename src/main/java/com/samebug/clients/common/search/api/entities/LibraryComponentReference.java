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
package com.samebug.clients.common.search.api.entities;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class LibraryComponentReference extends ComponentReference {
    @NotNull
    private Integer id;
    @Nullable
    private String mavenId;
    @NotNull
    private String name;
    @NotNull
    private String slug;
    @Nullable
    private String description;
    @NotNull
    private Integer color;

    @Override
    @NotNull
    public String getName() {
        return name;
    }

    @Override
    @NotNull
    public Integer getColor() {
        return color;
    }

    @NotNull
    public Integer getId() {
        return id;
    }

    @Nullable
    public String getMavenId() {
        return mavenId;
    }

    @NotNull
    public String getSlug() {
        return slug;
    }

    @Nullable
    public String getDescription() {
        return description;
    }
}
