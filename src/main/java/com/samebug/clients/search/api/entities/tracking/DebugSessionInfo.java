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
public class DebugSessionInfo {
    private final UUID id = UUID.randomUUID();
    private final String sessionType;

    public DebugSessionInfo(String sessionType) {
        this.sessionType = sessionType;
    }

    public UUID getId() {
        return id;
    }
    public String getSessionType() {
        return sessionType;
    }
}