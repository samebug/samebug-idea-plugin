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

import com.samebug.clients.common.api.client.ClientResponse;
import com.samebug.clients.common.api.client.SamebugClient;
import com.samebug.clients.common.api.entities.search.CreatedSearch;
import com.samebug.clients.common.api.entities.search.SearchDetails;
import com.samebug.clients.common.api.exceptions.SamebugClientException;

public final class SearchService {
    final ClientService clientService;
    final SearchStore store;

    public SearchService(ClientService clientService, SearchStore store) {
        this.clientService = clientService;
        this.store = store;
    }

    public CreatedSearch search(final String trace) throws SamebugClientException {
        final SamebugClient client = clientService.client;

        // TODO
        ClientService.ConnectionAwareHttpRequest<CreatedSearch> requestHandler =
                new ClientService.ConnectionAwareHttpRequest<CreatedSearch>() {
                    ClientResponse<CreatedSearch> request() {
                        return client.createSearch(trace);
                    }
                };
        return clientService.execute(requestHandler);
    }

    public SearchDetails get(final int searchId) throws SamebugClientException {
        final SamebugClient client = clientService.client;

        ClientService.ConnectionAwareHttpRequest<SearchDetails> requestHandler =
                new ClientService.ConnectionAwareHttpRequest<SearchDetails>() {
                    ClientResponse<SearchDetails> request() {
                        return client.getSearch(searchId);
                    }

                    protected void success(SearchDetails result) {
                        store.searches.put(searchId, result);
                    }

                    protected void fail(SamebugClientException e) {
                        store.searches.remove(searchId);
                    }
                };
        return clientService.execute(requestHandler);
    }


}
