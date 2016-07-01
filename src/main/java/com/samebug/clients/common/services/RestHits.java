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
package com.samebug.clients.common.services;

import com.samebug.clients.search.api.entities.RestHit;
import org.jetbrains.annotations.NotNull;

final public class RestHits {
    @NotNull
    public static RestHit asMarked(@NotNull final RestHit hit) {
        RestHit marked = new RestHit(hit);
        marked.score++;
        marked.markId = -1;
        return marked;
    }

    @NotNull
    public static RestHit asUnmarked(@NotNull final RestHit hit) {
        RestHit marked = new RestHit(hit);
        marked.score--;
        marked.markId = null;
        return marked;
    }
}
