package com.samebug.clients.common.services;

import com.intellij.util.messages.MessageBus;
import com.samebug.clients.common.entities.user.Statistics;
import com.samebug.clients.common.entities.user.User;
import com.samebug.clients.common.messages.ConnectionStatusListener;
import com.samebug.clients.common.messages.ProfileListener;
import com.samebug.clients.common.search.api.client.ClientResponse;
import com.samebug.clients.common.search.api.client.SamebugClient;
import com.samebug.clients.common.search.api.entities.UserInfo;
import com.samebug.clients.common.search.api.entities.UserStats;
import com.samebug.clients.common.search.api.exceptions.SamebugClientException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.atomic.AtomicReference;

final public class ProfileService {
    @NotNull
    final MessageBus messageBus;
    @NotNull
    final ClientService clientService;

    final AtomicReference<Integer> userId;
    final AtomicReference<Integer> workspaceId;

    @NotNull
    final AtomicReference<User> user;

    @NotNull
    final AtomicReference<Statistics> statistics;

    public ProfileService(@NotNull MessageBus messageBus, @NotNull ClientService clientService) {
        this.messageBus = messageBus;
        this.clientService = clientService;

        user = new AtomicReference<User>();
        statistics = new AtomicReference<Statistics>();
        this.userId = new AtomicReference<Integer>();
        this.workspaceId = new AtomicReference<Integer>();
    }

    @Nullable
    public User getUser() {
        return user.get();
    }

    @Nullable
    public Statistics getUserStats() {
        return statistics.get();
    }

    // TODO Settings dialog should call this before it tries to authenticate
    public void changeUserSettings(@Nullable Integer userId, @Nullable Integer workspaceId) {
        this.userId.set(userId);
        this.workspaceId.set(workspaceId);
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
                            userResult = new User(result.getUserId(), result.getDisplayName(), result.getAvatarUrl(), workspaceId.get());
                        } else {
                            userResult = null;
                        }
                        user.set(userResult);
                        // TODO rest api should accept the workspaceId, and tell if it is valid or not
                        // TODO rest api should return the default workspace id if not specified
                        // TODO someone should listen to this event in order to save userId and workspaceId to application settings when necessary
                        messageBus.syncPublisher(ProfileListener.TOPIC).profileChange(userResult, statistics.get());

                        // NOTE: this is a special case, we handle connection status by the result, not by the http status
                        boolean isUserAuthenticated = result.getUserExist();
                        clientService.authenticated.set(isUserAuthenticated);
                        messageBus.syncPublisher(ConnectionStatusListener.TOPIC).authenticationChange(isUserAuthenticated);
                    }

                    protected void fail(SamebugClientException e) {
                        user.set(null);
                        messageBus.syncPublisher(ProfileListener.TOPIC).profileChange(null, statistics.get());
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
                        statistics.set(statisticsResult);
                        messageBus.syncPublisher(ProfileListener.TOPIC).profileChange(user.get(), statisticsResult);
                    }

                    protected void fail(SamebugClientException e) {
                        statistics.set(null);
                        messageBus.syncPublisher(ProfileListener.TOPIC).profileChange(user.get(), null);
                    }
                };
        return clientService.execute(requestHandler);
    }
}
