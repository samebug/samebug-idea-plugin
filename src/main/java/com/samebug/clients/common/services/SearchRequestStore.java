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

import com.samebug.clients.common.entities.search.DebugSessionInfo;
import com.samebug.clients.common.entities.search.SearchRequest;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;

public final class SearchRequestStore {
    private final Map<DebugSessionInfo, Set<UUID>> sessions;
    private final Map<UUID, SearchRequest> requests;

    public SearchRequestStore() {
        requests = new ConcurrentHashMap<UUID, SearchRequest>();
        sessions = new ConcurrentHashMap<DebugSessionInfo, Set<UUID>>();
    }

    @Nullable
    public SearchRequest getRequest(UUID requestId) {
        return requests.get(requestId);
    }

    public Map<UUID, SearchRequest> getRequests(DebugSessionInfo sessionInfo) {
        Map<UUID, SearchRequest> results = new HashMap<UUID, SearchRequest>();
        Set<UUID> requestIdsInSession = sessions.get(sessionInfo);
        if (requestIdsInSession != null) {
            for (UUID requestId : requestIdsInSession) {
                results.put(requestId, requests.get(requestId));
            }
        }
        return results;
    }

    public void addRequest(SearchRequest request) {
        DebugSessionInfo sessionInfo = request.getSearchInfo().sessionInfo;
        UUID requestId = request.getSearchInfo().requestId;
        requests.put(requestId, request);
        Set<UUID> requestIds = sessions.get(sessionInfo);
        if (requestIds == null) requestIds = new ConcurrentSkipListSet<UUID>();
        requestIds.add(requestId);
        sessions.put(sessionInfo, requestIds);
    }

    public void removeRequest(UUID requestId) {
        SearchRequest searchRequest = requests.get(requestId);
        if (searchRequest != null) {
            DebugSessionInfo sessionInfo = searchRequest.getSearchInfo().sessionInfo;
            Set<UUID> session = sessions.get(sessionInfo);
            session.remove(requestId);
            sessions.put(sessionInfo, session);
            requests.remove(requestId);
        }
    }

    public void removeSession(DebugSessionInfo sessionInfo) {
        Set<UUID> requestIdsInSession = sessions.get(sessionInfo);
        sessions.remove(sessionInfo);

        if (requestIdsInSession != null) {
            for (UUID requestId : requestIdsInSession) {
                requests.remove(requestId);
            }
        }
    }


}
