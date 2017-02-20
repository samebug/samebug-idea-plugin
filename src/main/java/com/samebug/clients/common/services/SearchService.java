package com.samebug.clients.common.services;

import com.intellij.util.messages.MessageBus;
import com.samebug.clients.common.search.api.client.ClientResponse;
import com.samebug.clients.common.search.api.client.SamebugClient;
import com.samebug.clients.common.search.api.entities.SearchResults;
import com.samebug.clients.common.search.api.exceptions.SamebugClientException;

public final class SearchService {
    final ClientService clientService;
    final SearchStore searchStore;

    public SearchService(MessageBus messageBus, ClientService clientService, SearchStore searchStore) {
        this.clientService = clientService;
        this.searchStore = searchStore;
    }

    public SearchResults search(final String trace) throws SamebugClientException {
        final SamebugClient client = clientService.client;

        // TODO
        ClientService.ConnectionAwareHttpRequest<SearchResults> requestHandler =
                new ClientService.ConnectionAwareHttpRequest<SearchResults>() {
                    ClientResponse<SearchResults> request() {
                        return client.searchSolutions(trace);
                    }

                    protected void start() {
                    }

                    protected void success(SearchResults result) {
                    }

                    protected void fail(SamebugClientException e) {
                    }
                };
        return clientService.execute(requestHandler);
    }


}
