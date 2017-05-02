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
import com.samebug.clients.http.entities.helprequest.HelpRequest;
import com.samebug.clients.http.entities.helprequest.IncomingHelpRequestList;
import com.samebug.clients.http.exceptions.SamebugClientException;
import com.samebug.clients.http.form.HelpRequestCancel;
import com.samebug.clients.http.form.HelpRequestCreate;
import org.jetbrains.annotations.NotNull;

public final class HelpRequestService {
    final SamebugClient client;
    final HelpRequestStore store;

    public HelpRequestService(SamebugClient client, HelpRequestStore store) {
        this.client = client;
        this.store = store;
    }

    public IncomingHelpRequestList loadIncoming() throws SamebugClientException {
        IncomingHelpRequestList result = client.getIncomingHelpRequests();
        store.incoming = result;
        return result;
    }

    public HelpRequest createHelpRequest(@NotNull final HelpRequestCreate.Data data) throws SamebugClientException, HelpRequestCreate.BadRequest {
        return client.createHelpRequest(data);
    }

    public HelpRequest getHelpRequest(final String helpRequestId) throws SamebugClientException {
        return client.getHelpRequest(helpRequestId);
    }

    public HelpRequest revokeHelpRequest(final String helpRequestId) throws SamebugClientException, HelpRequestCancel.BadRequest {
        return client.revokeHelpRequest(helpRequestId);
    }
}
