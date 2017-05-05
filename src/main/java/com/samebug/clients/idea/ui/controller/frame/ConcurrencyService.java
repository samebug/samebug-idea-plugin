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
package com.samebug.clients.idea.ui.controller.frame;

import com.intellij.util.concurrency.FixedFuture;
import com.samebug.clients.common.services.*;
import com.samebug.clients.http.entities.helprequest.HelpRequest;
import com.samebug.clients.http.entities.profile.UserInfo;
import com.samebug.clients.http.entities.profile.UserStats;
import com.samebug.clients.http.entities.response.*;
import com.samebug.clients.http.exceptions.SamebugClientException;
import org.jetbrains.ide.PooledThreadExecutor;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

public final class ConcurrencyService {
    private final ExecutorService executor;
    private final ProfileStore profileStore;
    private final ProfileService profileService;
    private final SolutionStore solutionStore;
    private final SolutionService solutionService;
    private final HelpRequestStore helpRequestStore;
    private final HelpRequestService helpRequestService;
    private final SearchStore searchStore;
    private final SearchService searchService;

    public ConcurrencyService(ProfileStore profileStore, ProfileService profileService,
                              SolutionStore solutionStore, SolutionService solutionService,
                              HelpRequestStore helpRequestStore, HelpRequestService helpRequestService,
                              SearchStore searchStore, SearchService searchService) {
        this.solutionStore = solutionStore;
        this.profileStore = profileStore;
        this.profileService = profileService;
        this.solutionService = solutionService;
        this.helpRequestStore = helpRequestStore;
        this.helpRequestService = helpRequestService;
        this.searchStore = searchStore;
        this.searchService = searchService;
        executor = PooledThreadExecutor.INSTANCE;
    }

    public Future<UserInfo> userInfo() {
        UserInfo current = profileStore.getUser();
        if (current == null) return executor.submit(new Callable<UserInfo>() {
            @Override
            public UserInfo call() throws SamebugClientException {
                return profileService.loadUserInfo();
            }
        });
        else return new FixedFuture<UserInfo>(current);
    }

    public Future<UserStats> userStats() {
        UserStats current = profileStore.getUserStats();
        if (current == null) return executor.submit(new Callable<UserStats>() {
            @Override
            public UserStats call() throws SamebugClientException {
                return profileService.loadUserStats();
            }
        });
        else return new FixedFuture<UserStats>(current);
    }

    public Future<GetSolutions> solutions(final int searchId) {
        GetSolutions current = solutionStore.getWebHits(searchId);
        if (current == null) return executor.submit(new Callable<GetSolutions>() {
            @Override
            public GetSolutions call() throws SamebugClientException {
                return solutionService.loadWebHits(searchId);
            }
        });
        else return new FixedFuture<GetSolutions>(current);
    }

    public Future<GetTips> tips(final int searchId) {
        GetTips current = solutionStore.getTipHits(searchId);
        if (current == null) return executor.submit(new Callable<GetTips>() {
            @Override
            public GetTips call() throws SamebugClientException {
                return solutionService.loadTipHits(searchId);
            }
        });
        else return new FixedFuture<GetTips>(current);
    }

    public Future<GetBugmates> bugmates(final int searchId) {
        GetBugmates current = solutionStore.getBugmates(searchId);
        if (current == null) return executor.submit(new Callable<GetBugmates>() {
            @Override
            public GetBugmates call() throws SamebugClientException {
                return solutionService.loadBugmates(searchId);
            }
        });
        else return new FixedFuture<GetBugmates>(current);
    }

    public Future<IncomingHelpRequestList> incomingHelpRequests(boolean forceReload) {
        IncomingHelpRequestList current = helpRequestStore.get();
        if (current == null || forceReload) return executor.submit(new Callable<IncomingHelpRequestList>() {
            @Override
            public IncomingHelpRequestList call() throws SamebugClientException {
                return helpRequestService.loadIncoming();
            }
        });
        else return new FixedFuture<IncomingHelpRequestList>(current);
    }

    public Future<HelpRequest> helpRequest(final String helpRequestId) {
        return executor.submit(new Callable<HelpRequest>() {
            @Override
            public HelpRequest call() throws SamebugClientException {
                return helpRequestService.getHelpRequest(helpRequestId);
            }
        });
    }

    public Future<CreatedSearch> search(final int searchId) {
        return executor.submit(new Callable<CreatedSearch>() {
            @Override
            public CreatedSearch call() throws SamebugClientException {
                return searchService.get(searchId);
            }
        });
    }
}
