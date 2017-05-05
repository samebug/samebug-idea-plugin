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
import com.samebug.clients.http.entities.profile.UserStats;
import com.samebug.clients.http.entities.user.Me;
import com.samebug.clients.http.exceptions.SamebugClientException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class ProfileService {
    @NotNull
    final SamebugClient client;
    @NotNull
    final ProfileStore store;


    public ProfileService(@NotNull SamebugClient client, @NotNull ProfileStore store) {
        this.client = client;
        this.store = store;
    }

    @Nullable
    public Me loadUserInfo() throws SamebugClientException {
        try {
            Me result = client.getUserInfo();
            store.user.set(result);
            return result;
        } catch (SamebugClientException e) {
            return null;
        }
    }

    public UserStats loadUserStats() throws SamebugClientException {
        UserStats result = client.getUserStats();
        store.statistics.set(result);
        return result;
    }
}
