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
package com.samebug.clients.common.search.api.entities;

import org.jetbrains.annotations.NotNull;

public final class Source {
    @NotNull
    private String name;
    @NotNull
    private String icon;

    @NotNull
    public String getName() {
        return name;
    }

    @NotNull
    public String getIcon() {
        return icon;
    }

    public Source() {
    }

    public Source(@NotNull Source rhs) {
        this(rhs.name, rhs.icon);
    }

    public Source(@NotNull String name, @NotNull String icon) {
        this.name = name;
        this.icon = icon;
    }
}
