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

public final class DebugSessionInfo {
    public final UUID id = UUID.randomUUID();
    public final String sessionType;

    public DebugSessionInfo(String sessionType) {
        this.sessionType = sessionType;
    }

    public UUID getId() {
        return id;
    }

    public String getSessionType() {
        return sessionType;
    }


    @Override
    public String toString() {
        return "Session " + sessionType + "/" + id;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof DebugSessionInfo) {
            DebugSessionInfo rhs = (DebugSessionInfo) o;
            return id.equals(rhs.id);
        } else return false;
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
