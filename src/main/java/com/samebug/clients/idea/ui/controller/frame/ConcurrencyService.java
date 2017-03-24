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
import com.samebug.clients.common.api.entities.bugmate.BugmatesResult;
import com.samebug.clients.common.api.entities.helpRequest.IncomingHelpRequests;
import com.samebug.clients.common.api.entities.helpRequest.MatchingHelpRequest;
import com.samebug.clients.common.api.entities.profile.UserInfo;
import com.samebug.clients.common.api.entities.profile.UserStats;
import com.samebug.clients.common.api.entities.search.SearchDetails;
import com.samebug.clients.common.api.entities.solution.Solutions;
import com.samebug.clients.common.api.exceptions.SamebugClientException;
import com.samebug.clients.common.services.*;
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
    private final BugmateStore bugmateStore;
    private final BugmateService bugmateService;
    private final HelpRequestStore helpRequestStore;
    private final HelpRequestService helpRequestService;
    private final SearchStore searchStore;
    private final SearchService searchService;

    public ConcurrencyService(ProfileStore profileStore, ProfileService profileService,
                              SolutionStore solutionStore, SolutionService solutionService,
                              BugmateStore bugmateStore, BugmateService bugmateService,
                              HelpRequestStore helpRequestStore, HelpRequestService helpRequestService,
                              SearchStore searchStore, SearchService searchService) {
        this.solutionStore = solutionStore;
        this.profileStore = profileStore;
        this.profileService = profileService;
        this.solutionService = solutionService;
        this.bugmateStore = bugmateStore;
        this.bugmateService = bugmateService;
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

    public Future<Solutions> solutions(final int searchId) {
        Solutions current = solutionStore.get(searchId);
        if (current == null) return executor.submit(new Callable<Solutions>() {
            @Override
            public Solutions call() throws SamebugClientException {
                return solutionService.loadSolutions(searchId);
            }
        });
        else return new FixedFuture<Solutions>(current);
    }

    public Future<BugmatesResult> bugmates(final int searchId) {
        BugmatesResult current = bugmateStore.get(searchId);
        if (current == null) return executor.submit(new Callable<BugmatesResult>() {
            @Override
            public BugmatesResult call() throws SamebugClientException {
                return bugmateService.loadBugmates(searchId);
            }
        });
        else return new FixedFuture<BugmatesResult>(current);
    }

    public Future<IncomingHelpRequests> incomingHelpRequests(boolean forceReload) {
        IncomingHelpRequests current = helpRequestStore.get();
        if (current == null || forceReload) return executor.submit(new Callable<IncomingHelpRequests>() {
            @Override
            public IncomingHelpRequests call() throws SamebugClientException {
                return helpRequestService.loadIncoming();
            }
        });
        else return new FixedFuture<IncomingHelpRequests>(current);
    }

    public Future<MatchingHelpRequest> helpRequest(final String helpRequestId) {
        return executor.submit(new Callable<MatchingHelpRequest>() {
            @Override
            public MatchingHelpRequest call() throws SamebugClientException {
                return helpRequestService.getHelpRequest(helpRequestId);
            }
        });
    }

    public Future<SearchDetails> search(final int searchId) {
        return executor.submit(new Callable<SearchDetails>() {
            @Override
            public SearchDetails call() throws SamebugClientException {
                return searchService.get(searchId);
            }
        });
    }
}
