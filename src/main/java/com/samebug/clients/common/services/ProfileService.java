package com.samebug.clients.common.services;

import com.intellij.util.messages.MessageBus;
import com.samebug.clients.common.entities.user.Statistics;
import com.samebug.clients.common.entities.user.User;
import com.samebug.clients.common.messages.AuthenticationListener;
import com.samebug.clients.common.messages.ConnectionStatusListener;
import com.samebug.clients.common.messages.ProfileListener;
import com.samebug.clients.common.search.api.client.ClientResponse;
import com.samebug.clients.common.search.api.client.SamebugClient;
import com.samebug.clients.common.search.api.entities.UserInfo;
import com.samebug.clients.common.search.api.entities.UserStats;
import com.samebug.clients.common.search.api.exceptions.SamebugClientException;
import org.jetbrains.annotations.NotNull;

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

    public UserInfo loadUserInfo(final String apiKey) throws SamebugClientException {
        final SamebugClient client = clientService.client;

        ClientService.ConnectionAwareHttpRequest<UserInfo> requestHandler =
                new ClientService.ConnectionAwareHttpRequest<UserInfo>() {
                    ClientResponse<UserInfo> request() {
                        return client.getUserInfo(apiKey);
                    }

                    protected void success(UserInfo result) {
                        final User userResult;
                        if (result.getUserExist()) {
                            userResult = new User(result.getUserId(), result.getDisplayName(), result.getAvatarUrl(), store.workspaceId.get());
                        } else {
                            userResult = null;
                        }
                        store.user.set(userResult);
                        messageBus.syncPublisher(ProfileListener.TOPIC).profileChange(userResult, store.statistics.get());

                        // NOTE: this is a special case, we handle connection status by the result, not by the http status
                        boolean isUserAuthenticated = result.getUserExist();
                        clientService.authenticated.set(isUserAuthenticated);
                        messageBus.syncPublisher(ConnectionStatusListener.TOPIC).authenticationChange(isUserAuthenticated);
                    }

                    protected void fail(SamebugClientException e) {
                        store.user.set(null);
                        messageBus.syncPublisher(ProfileListener.TOPIC).profileChange(null, store.statistics.get());
                    }
                };
        return clientService.execute(requestHandler);
    }

    public UserStats loadUserStats() throws SamebugClientException {
        final SamebugClient client = clientService.client;

        ClientService.ConnectionAwareHttpRequest<UserStats> requestHandler =
                new ClientService.ConnectionAwareHttpRequest<UserStats>() {
                    ClientResponse<UserStats> request() {
                        return client.getUserStats();
                    }

                    protected void success(UserStats result) {
                        final Statistics statisticsResult = new Statistics(result.getNumberOfTips(), result.getNumberOfMarks(), result.getNumberOfThanks());
                        store.statistics.set(statisticsResult);
                        messageBus.syncPublisher(ProfileListener.TOPIC).profileChange(store.user.get(), statisticsResult);
                    }

                    protected void fail(SamebugClientException e) {
                        store.statistics.set(null);
                        messageBus.syncPublisher(ProfileListener.TOPIC).profileChange(store.user.get(), null);
                    }
                };
        return clientService.execute(requestHandler);
    }

    public UserInfo authenticate(final String apiKey) throws SamebugClientException {
        final SamebugClient client = clientService.client;

        ClientService.ConnectionAwareHttpRequest<UserInfo> requestHandler =
                new ClientService.ConnectionAwareHttpRequest<UserInfo>() {
                    ClientResponse<UserInfo> request() {
                        return client.getUserInfo(apiKey);
                    }

                    protected void success(UserInfo result) {
                        // TODO save authentication response?
                        // TODO rest api should accept the workspaceId, and tell if it is valid or not
                        // TODO rest api should return the default workspace id if not specified
                        if (result.getUserExist()) {
                            messageBus.syncPublisher(AuthenticationListener.TOPIC).success(apiKey);
                        } else {
                            messageBus.syncPublisher(AuthenticationListener.TOPIC).fail();
                        }
                    }

                    protected void fail(SamebugClientException e) {
                        messageBus.syncPublisher(AuthenticationListener.TOPIC).fail();
                    }
                };
        return clientService.execute(requestHandler);
    }
}
