package com.samebug.clients.common.services;

import com.intellij.util.messages.MessageBus;
import com.samebug.clients.common.search.api.client.ClientResponse;
import com.samebug.clients.common.search.api.client.SamebugClient;
import com.samebug.clients.common.search.api.entities.UserInfo;
import com.samebug.clients.common.search.api.entities.UserStats;
import com.samebug.clients.common.search.api.exceptions.SamebugClientException;
import com.samebug.clients.idea.components.application.IdeaSamebugPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class ProfileService {
    @NotNull
    final MessageBus messageBus;
    @NotNull
    final ClientService clientService;
    @NotNull
    final ProfileStore store;


    public ProfileService(@NotNull MessageBus messageBus, @NotNull ClientService clientService, @NotNull ProfileStore store) {
        this.messageBus = messageBus;
        this.clientService = clientService;
        this.store = store;
    }

    // TODO this is a bit different from other services, as ClientService.getUserInfo has two different purpose currently
    // - check if an apiKey is valid
    // - return profile information about the user
    // When it will be separated, this method won't have to read the application settings.
    @Nullable
    public UserInfo loadUserInfo() throws SamebugClientException {
        final SamebugClient client = clientService.client;
        final String apiKey = IdeaSamebugPlugin.getInstance().getState().apiKey;

        if (apiKey == null) return null;
        else {
            ClientService.ConnectionAwareHttpRequest<UserInfo> requestHandler =
                    new ClientService.ConnectionAwareHttpRequest<UserInfo>() {
                        ClientResponse<UserInfo> request() {
                            return client.getUserInfo(apiKey);
                        }

                        protected void success(UserInfo result) {
                            store.user.set(result);
                        }

                        protected void fail(SamebugClientException e) {
                            store.user.set(null);
                        }
                    };
            return clientService.execute(requestHandler);
        }
    }

    public UserStats loadUserStats() throws SamebugClientException {
        final SamebugClient client = clientService.client;

        ClientService.ConnectionAwareHttpRequest<UserStats> requestHandler =
                new ClientService.ConnectionAwareHttpRequest<UserStats>() {
                    ClientResponse<UserStats> request() {
                        return client.getUserStats();
                    }

                    protected void success(UserStats result) {
                        store.statistics.set(result);
                    }

                    protected void fail(SamebugClientException e) {
                        store.statistics.set(null);
                    }
                };
        return clientService.execute(requestHandler);
    }
}
