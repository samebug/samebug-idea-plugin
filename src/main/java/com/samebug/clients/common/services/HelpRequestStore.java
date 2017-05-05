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
package com.samebug.clients.common.services;

import com.samebug.clients.http.entities.helprequest.HelpRequest;
import com.samebug.clients.http.entities.helprequest.HelpRequestMatch;
import com.samebug.clients.http.entities.jsonapi.IncomingHelpRequestList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class HelpRequestStore {
    @Nullable
    IncomingHelpRequestList incoming;

    public HelpRequestStore() {

    }

    public IncomingHelpRequestList get() {
        return incoming;
    }

    @Nullable
    public HelpRequest getHelpRequest(@NotNull final String id) {
        HelpRequest match = null;
        if (incoming != null) {
            for (HelpRequestMatch h : incoming.getData()) {
                if (id.equals(h.getHelpRequest().getId())) match = h.getHelpRequest();
            }
        }
        return match;
    }

    public void invalidate() {
        incoming = null;
    }
}
