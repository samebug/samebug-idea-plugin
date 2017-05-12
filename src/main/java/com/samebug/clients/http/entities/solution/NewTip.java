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
package com.samebug.clients.http.entities.solution;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class NewTip {
    @NotNull
    public final String type = "new--tip";
    @NotNull
    public final String message;
    @Nullable
    public final String sourceUrl;

    public NewTip(@NotNull final String message, @Nullable final String sourceUrl) {
        this.message = message;
        this.sourceUrl = sourceUrl;
    }
}
