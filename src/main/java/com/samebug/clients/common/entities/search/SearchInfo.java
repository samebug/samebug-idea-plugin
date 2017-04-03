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
package com.samebug.clients.common.entities.search;

import java.util.UUID;

public final class SearchInfo {
    public final DebugSessionInfo sessionInfo;
    public final UUID requestId = UUID.randomUUID();

    public SearchInfo(DebugSessionInfo sessionInfo) {
        this.sessionInfo = sessionInfo;
    }

    public DebugSessionInfo getSessionInfo() {
        return sessionInfo;
    }

    public UUID getRequestId() {
        return requestId;
    }

    @Override
    public String toString() {
        return "Request " + requestId + " in session " + sessionInfo.sessionType + "/" + sessionInfo.id;
    }
}
