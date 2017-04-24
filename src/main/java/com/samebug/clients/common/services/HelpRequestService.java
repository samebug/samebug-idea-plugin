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
import com.samebug.clients.http.entities.helpRequest.MatchingHelpRequest;
import com.samebug.clients.http.entities.helpRequest.MyHelpRequest;
import com.samebug.clients.http.entities.helprequest.IncomingHelpRequests;
import com.samebug.clients.http.exceptions.SamebugClientException;
import com.samebug.clients.http.form.HelpRequestCancel;
import com.samebug.clients.http.form.HelpRequestCreate;
import org.jetbrains.annotations.NotNull;

public final class HelpRequestService {
    final ClientService clientService;
    final HelpRequestStore store;

    public HelpRequestService(ClientService clientService, HelpRequestStore store) {
        this.clientService = clientService;
        this.store = store;
    }

    public IncomingHelpRequests loadIncoming() throws SamebugClientException {
        final SamebugClient client = clientService.client;
        IncomingHelpRequests result = client.getIncomingHelpRequests();
        store.incoming = result;
        return result;
    }

    public MyHelpRequest createHelpRequest(@NotNull final HelpRequestCreate.Data data) throws SamebugClientException, HelpRequestCreate.BadRequest {
        final SamebugClient client = clientService.client;
        // TODO
        client.createHelpRequest(data);
        return null;
    }

    public MatchingHelpRequest getHelpRequest(final String helpRequestId) throws SamebugClientException {
        final SamebugClient client = clientService.client;
        // TODO
        return null; //client.getHelpRequest(helpRequestId);
    }

    public MyHelpRequest revokeHelpRequest(final String helpRequestId) throws SamebugClientException, HelpRequestCancel.BadRequest {
        final SamebugClient client = clientService.client;
        // TODO
        client.revokeHelpRequest(helpRequestId);
        return null;
    }
}
