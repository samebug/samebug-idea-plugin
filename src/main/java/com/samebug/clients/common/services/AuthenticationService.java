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
import com.samebug.clients.common.api.entities.UserInfo;
import com.samebug.clients.common.api.exceptions.SamebugClientException;
import org.jetbrains.annotations.Nullable;

public final class AuthenticationService {
    final ClientService clientService;

    public AuthenticationService(ClientService clientService) {
        this.clientService = clientService;
    }

    public UserInfo apiKeyAuthentication(final String apiKey, @Nullable final Integer workspaceId) throws SamebugClientException {
        final SamebugClient client = clientService.client;

        ClientService.ConnectionAwareHttpRequest<UserInfo> requestHandler =
                new ClientService.ConnectionAwareHttpRequest<UserInfo>() {
                    ClientResponse<UserInfo> request() {
                        // TODO the server should accept the workspaceId
                        return client.getUserInfo(apiKey);
                    }

                    protected void success(UserInfo result) {
                        // NOTE: this is a special case, we handle connection status by the result, not by the http status
                        clientService.updateAuthenticated(result.getUserExist());
                        // TODO tell the client service if there is a problem with the workspace
                        // TODO if workspaceId is null, save the returned default workspace id to application settings.

                    }
                };
        return clientService.execute(requestHandler);
    }


}
