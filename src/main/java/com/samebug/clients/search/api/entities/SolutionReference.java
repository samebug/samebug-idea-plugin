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
import org.jetbrains.annotations.Nullable;

import java.net.URL;
import java.util.Date;

public final class SolutionReference extends RestSolution {
    @NotNull
    public Source source;
    @Nullable
    public Author author;
    @NotNull
    public String title;
    @NotNull
    public URL url;

    public SolutionReference(@NotNull final SolutionReference rhs) {
        this.createdAt = new Date(rhs.createdAt.getTime());
        this.source = new Source(rhs.source);
        this.author = rhs.author == null ? null : new Author(rhs.author);
        this.title = rhs.title;
        this.url = rhs.url;
    }
}
