package com.samebug.clients.common.services;

import com.intellij.util.messages.MessageBus;
import com.samebug.clients.common.search.api.client.ClientResponse;
import com.samebug.clients.common.search.api.client.SamebugClient;
import com.samebug.clients.common.search.api.entities.UserInfo;
import com.samebug.clients.common.search.api.exceptions.SamebugClientException;
import org.jetbrains.annotations.Nullable;

public final class AuthenticationService {
    final MessageBus messageBus;
    final ClientService clientService;

    public AuthenticationService(MessageBus messageBus, ClientService clientService) {
        this.messageBus = messageBus;
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
