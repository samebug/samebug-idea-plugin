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

import com.samebug.clients.http.response.ClientResponse;
import com.samebug.clients.http.client.SamebugClient;
import com.samebug.clients.http.entities.profile.UserInfo;
import com.samebug.clients.http.entities.profile.UserStats;
import com.samebug.clients.http.exceptions.SamebugClientException;
import com.samebug.clients.idea.components.application.IdeaSamebugPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class ProfileService {
    @NotNull
    final ClientService clientService;
    @NotNull
    final ProfileStore store;


    public ProfileService(@NotNull ClientService clientService, @NotNull ProfileStore store) {
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
