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

import com.samebug.clients.common.search.api.client.ClientResponse;
import com.samebug.clients.common.search.api.client.SamebugClient;
import com.samebug.clients.common.search.api.entities.SearchResults;
import com.samebug.clients.common.search.api.exceptions.SamebugClientException;

public final class SearchService {
    final ClientService clientService;
    final SearchStore searchStore;

    public SearchService(ClientService clientService, SearchStore searchStore) {
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
