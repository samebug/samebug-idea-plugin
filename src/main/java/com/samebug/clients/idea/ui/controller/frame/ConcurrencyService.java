/*
 * Copyright 2018 Samebug, Inc.
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
package com.samebug.clients.idea.ui.controller.frame;

import com.intellij.util.concurrency.FixedFuture;
import com.samebug.clients.common.services.ProfileService;
import com.samebug.clients.common.services.ProfileStore;
import com.samebug.clients.common.services.SearchService;
import com.samebug.clients.http.entities.search.Search;
import com.samebug.clients.http.entities.user.Me;
import com.samebug.clients.http.exceptions.SamebugClientException;
import org.jetbrains.ide.PooledThreadExecutor;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

public final class ConcurrencyService {
    private final ExecutorService executor;
    private final ProfileStore profileStore;
    private final ProfileService profileService;
    private final SearchService searchService;

    public ConcurrencyService(ProfileStore profileStore, ProfileService profileService,
                              SearchService searchService) {
        this.profileStore = profileStore;
        this.profileService = profileService;
        this.searchService = searchService;
        executor = PooledThreadExecutor.INSTANCE;
    }

    public Future<Me> userInfo() {
        Me current = profileStore.getUser();
        if (current == null) return executor.submit(new Callable<Me>() {
            @Override
            public Me call() throws SamebugClientException {
                return profileService.loadUserInfo();
            }
        });
        else return new FixedFuture<Me>(current);
    }

    public Future<Search> search(final int searchId) {
        return executor.submit(new Callable<Search>() {
            @Override
            public Search call() throws SamebugClientException {
                return searchService.get(searchId);
            }
        });
    }
}
