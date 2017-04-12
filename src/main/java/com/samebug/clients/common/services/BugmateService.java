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

import com.samebug.clients.http.client.SamebugClient;
import com.samebug.clients.http.entities.bugmate.BugmatesResult;
import com.samebug.clients.http.exceptions.SamebugClientException;

public final class BugmateService {
    final ClientService clientService;
    final BugmateStore bugmateStore;

    public BugmateService(ClientService clientService, BugmateStore bugmateStore) {
        this.clientService = clientService;
        this.bugmateStore = bugmateStore;
    }

    public BugmatesResult loadBugmates(final int searchId) throws SamebugClientException {
        final SamebugClient client = clientService.client;
        BugmatesResult result = client.getBugmates(searchId);
        bugmateStore.bugmates.put(searchId, result);
        return result;
    }
}
