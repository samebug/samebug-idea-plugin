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
package com.samebug.clients.common.services;

import com.intellij.util.messages.MessageBus;
import com.samebug.clients.common.search.api.client.ClientResponse;
import com.samebug.clients.common.search.api.client.SamebugClient;
import com.samebug.clients.common.search.api.entities.BugmatesResult;
import com.samebug.clients.common.search.api.exceptions.SamebugClientException;

public final class BugmateService {
    final MessageBus messageBus;
    final ClientService clientService;
    final BugmateStore bugmateStore;

    public BugmateService(MessageBus messageBus, ClientService clientService, BugmateStore bugmateStore) {
        this.messageBus = messageBus;
        this.clientService = clientService;
        this.bugmateStore = bugmateStore;
    }

    public BugmatesResult loadBugmates(final int searchId) throws SamebugClientException {
        final SamebugClient client = clientService.client;

        ClientService.ConnectionAwareHttpRequest<BugmatesResult> requestHandler =
                new ClientService.ConnectionAwareHttpRequest<BugmatesResult>() {
                    ClientResponse<BugmatesResult> request() {
                        return client.getBugmates(searchId);
                    }

                    protected void start() {
                    }

                    protected void success(BugmatesResult result) {
                        bugmateStore.bugmates.put(searchId, result);
                    }

                    protected void fail(SamebugClientException e) {
                        bugmateStore.bugmates.remove(searchId);
                    }
                };
        return clientService.execute(requestHandler);
    }
}
