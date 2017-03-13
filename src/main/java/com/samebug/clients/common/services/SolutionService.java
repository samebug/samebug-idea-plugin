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
import com.samebug.clients.common.api.entities.solution.MarkResponse;
import com.samebug.clients.common.api.entities.solution.RestHit;
import com.samebug.clients.common.api.entities.solution.Solutions;
import com.samebug.clients.common.api.entities.solution.Tip;
import com.samebug.clients.common.api.exceptions.SamebugClientException;

public final class SolutionService {
    final ClientService clientService;
    final SolutionStore solutionStore;

    public SolutionService(ClientService clientService, SolutionStore solutionStore) {
        this.clientService = clientService;
        this.solutionStore = solutionStore;
    }

    public Solutions loadSolutions(final int searchId) throws SamebugClientException {
        final SamebugClient client = clientService.client;

        ClientService.ConnectionAwareHttpRequest<Solutions> requestHandler =
                new ClientService.ConnectionAwareHttpRequest<Solutions>() {
                    ClientResponse<Solutions> request() {
                        return client.getSolutions(searchId);
                    }

                    protected void success(Solutions result) {
                        solutionStore.solutions.put(searchId, result);
                    }

                    protected void fail(SamebugClientException e) {
//                        solutionStore.solutions.remove(searchId);
                    }
                };
        return clientService.execute(requestHandler);
    }

    public RestHit<Tip> postTip(final int searchId, final String tip, final String sourceUrl) throws SamebugClientException {
        final SamebugClient client = clientService.client;

        ClientService.ConnectionAwareHttpRequest<RestHit<Tip>> requestHandler =
                new ClientService.ConnectionAwareHttpRequest<RestHit<Tip>>() {
                    ClientResponse<RestHit<Tip>> request() {
                        return client.createTip(searchId, tip, sourceUrl);
                    }

                    protected void success(RestHit<Tip> response) {
                        // TODO thread safety
                        solutionStore.solutions.get(searchId).getTips().add(0, response);
                    }
                };
        return clientService.execute(requestHandler);
    }

    public MarkResponse postMark(final int searchId, final int solutionId) throws SamebugClientException {
        final SamebugClient client = clientService.client;

        ClientService.ConnectionAwareHttpRequest<MarkResponse> requestHandler =
                new ClientService.ConnectionAwareHttpRequest<MarkResponse>() {
                    ClientResponse<MarkResponse> request() {
                        return client.postMark(searchId, solutionId);
                    }
                };
        return clientService.execute(requestHandler);
    }

    public MarkResponse retractMark(final int voteId) throws SamebugClientException {
        final SamebugClient client = clientService.client;

        ClientService.ConnectionAwareHttpRequest<MarkResponse> requestHandler =
                new ClientService.ConnectionAwareHttpRequest<MarkResponse>() {
                    ClientResponse<MarkResponse> request() {
                        return client.retractMark(voteId);
                    }
                };
        return clientService.execute(requestHandler);
    }
}
