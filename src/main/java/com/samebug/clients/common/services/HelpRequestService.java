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
import com.samebug.clients.common.api.entities.helpRequest.MyHelpRequest;
import com.samebug.clients.common.api.entities.helpRequest.IncomingHelpRequests;
import com.samebug.clients.common.api.exceptions.SamebugClientException;

public final class HelpRequestService {
    final ClientService clientService;
    final HelpRequestStore store;

    public HelpRequestService(ClientService clientService, HelpRequestStore store) {
        this.clientService = clientService;
        this.store = store;
    }

    public IncomingHelpRequests loadIncoming() throws SamebugClientException {
        final SamebugClient client = clientService.client;

        ClientService.ConnectionAwareHttpRequest<IncomingHelpRequests> requestHandler =
                new ClientService.ConnectionAwareHttpRequest<IncomingHelpRequests>() {
                    ClientResponse<IncomingHelpRequests> request() {
                        return client.getIncomingHelpRequests();
                    }

                    protected void success(IncomingHelpRequests result) {
                        store.incoming = result;
                    }

                    protected void fail(SamebugClientException e) {
//                        TODO why would we remove it if once we found it?
//                        store.incoming = null;
                    }
                };
        return clientService.execute(requestHandler);
    }

    public MyHelpRequest createHelpRequest(final int searchId, final String context) throws SamebugClientException {
        final SamebugClient client = clientService.client;

        ClientService.ConnectionAwareHttpRequest<MyHelpRequest> requestHandler =
                new ClientService.ConnectionAwareHttpRequest<MyHelpRequest>() {
                    ClientResponse<MyHelpRequest> request() {
                        return client.createHelpRequest(searchId, context);
                    }
                };
        return clientService.execute(requestHandler);
    }

    public MyHelpRequest revokeHelpRequest(final String helpRequestId) throws SamebugClientException {
        final SamebugClient client = clientService.client;

        ClientService.ConnectionAwareHttpRequest<MyHelpRequest> requestHandler =
                new ClientService.ConnectionAwareHttpRequest<MyHelpRequest>() {
                    ClientResponse<MyHelpRequest> request() {
                        return client.revokeHelpRequest(helpRequestId);
                    }
                };
        return clientService.execute(requestHandler);
    }
}
