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
import com.samebug.clients.http.entities.helprequest.NewHelpRequest;
import com.samebug.clients.http.entities.jsonapi.IncomingHelpRequestList;
import com.samebug.clients.http.exceptions.SamebugClientException;
import com.samebug.clients.http.form.HelpRequestCancel;
import com.samebug.clients.http.form.HelpRequestCreate;
import org.jetbrains.annotations.NotNull;

public final class HelpRequestService {
    @NotNull
    final ClientService clientService;
    @NotNull
    final HelpRequestStore store;

    public HelpRequestService(@NotNull final ClientService clientService, @NotNull final HelpRequestStore store) {
        this.clientService = clientService;
        this.store = store;
    }

    @NotNull
    public IncomingHelpRequestList loadIncoming() throws SamebugClientException {
        IncomingHelpRequestList result = clientService.getClient().getIncomingHelpRequests();
        store.incoming = result;
        return result;
    }

    @NotNull
    public HelpRequest createHelpRequest(@NotNull final Integer searchId, @NotNull final NewHelpRequest data) throws SamebugClientException, HelpRequestCreate.BadRequest {
        return clientService.getClient().createHelpRequest(searchId, data);
    }

    @NotNull
    public HelpRequest getHelpRequest(@NotNull final String helpRequestId) throws SamebugClientException {
        return clientService.getClient().getHelpRequest(helpRequestId);
    }

    @NotNull
    public HelpRequest revokeHelpRequest(@NotNull final String helpRequestId) throws SamebugClientException, HelpRequestCancel.BadRequest {
        return clientService.getClient().cancelHelpRequest(helpRequestId);
    }
}
