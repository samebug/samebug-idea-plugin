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
package com.samebug.clients.search.api.entities.tracking;

import java.util.UUID;

/**
 * Created by poroszd on 2/19/16.
 */
public class SearchInfo {
    private final DebugSessionInfo sessionInfo;
    private final UUID requestId = UUID.randomUUID();

    public SearchInfo(DebugSessionInfo sessionInfo) {
        this.sessionInfo = sessionInfo;
    }

    public DebugSessionInfo getSessionInfo() {
        return sessionInfo;
    }

    public UUID getRequestId() {
        return requestId;
    }
}
