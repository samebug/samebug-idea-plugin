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
package com.samebug.clients.idea.search;

import com.intellij.util.messages.Topic;
import com.samebug.clients.common.entities.search.SavedSearch;

import java.util.UUID;

// TODO extend this interface to introduce more detailed search results, like text search, tip/bugmate results, etc.
public interface SearchRequestListener {
    Topic<SearchRequestListener> TOPIC = Topic.create("Request stacktrace search", SearchRequestListener.class);

    void failed(UUID requestId);

    void saved(UUID requestId, SavedSearch savedSearch);

    void searched(UUID requestId);
}
