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
