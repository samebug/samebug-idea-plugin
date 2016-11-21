/**
 * Copyright 2016 Samebug, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.samebug.clients.idea.services;

import com.intellij.openapi.project.Project;
import com.intellij.util.containers.HashMap;
import com.intellij.util.messages.MessageBusConnection;
import com.samebug.clients.common.entities.search.Requested;
import com.samebug.clients.common.entities.search.Saved;
import com.samebug.clients.common.entities.search.SearchRequest;
import com.samebug.clients.common.search.api.entities.SearchResults;
import com.samebug.clients.common.search.api.entities.tracking.DebugSessionInfo;
import com.samebug.clients.common.search.api.entities.tracking.SearchInfo;
import com.samebug.clients.common.search.api.exceptions.SamebugClientException;
import com.samebug.clients.idea.components.project.RunDebugWatcher;
import com.samebug.clients.idea.messages.console.SearchRequestListener;
import com.samebug.clients.idea.messages.model.StackTraceSearchListener;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;

final public class SessionService implements StackTraceSearchListener {
    private final Project myProject;
    private final Map<DebugSessionInfo, Set<UUID>> sessions;
    private final Map<UUID, SearchRequest> requests;

    public SessionService(Project project) {
        myProject = project;
        requests = new ConcurrentHashMap<UUID, SearchRequest>();
        sessions = new ConcurrentHashMap<DebugSessionInfo, Set<UUID>>();

        MessageBusConnection messageBusConnection = myProject.getMessageBus().connect(myProject);
        messageBusConnection.subscribe(StackTraceSearchListener.TOPIC, this);
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

    public void removeSession(DebugSessionInfo sessionInfo) {
        Set<UUID> requestIdsInSession = sessions.get(sessionInfo);
        sessions.remove(sessionInfo);

        if (requestIdsInSession != null) {
            for (UUID requestId : requestIdsInSession) {
                requests.remove(requestId);
            }
        }
    }

    @Override
    public void searchStart(SearchInfo searchInfo, String stackTrace) {
        Requested request = new Requested(stackTrace);
        UUID requestId = searchInfo.getRequestId();
        DebugSessionInfo sessionInfo = searchInfo.getSessionInfo();
        requests.put(requestId, request);
        Set<UUID> requestIds = sessions.get(sessionInfo);
        if (requestIds == null) requestIds = new ConcurrentSkipListSet<UUID>();
        requestIds.add(requestId);
        sessions.put(sessionInfo, requestIds);
    }

    @Override
    public void searchSucceeded(SearchInfo searchInfo, SearchResults result) {
        UUID requestId = searchInfo.getRequestId();
        SearchRequest r = requests.get(requestId);
        if (r != null && result.getStackTraceId() != null) {
            Saved saved = new Saved(r.getTrace(), result);
            requests.put(requestId, saved);
            myProject.getMessageBus().syncPublisher(SearchRequestListener.TOPIC).saved(requestId, saved);
        } else {
            requests.remove(requestId);
            myProject.getMessageBus().syncPublisher(SearchRequestListener.TOPIC).failed(requestId);
        }
    }

    @Override
    public void timeout(SearchInfo searchInfo) {
        UUID requestId = searchInfo.getRequestId();
        requests.remove(requestId);
        myProject.getMessageBus().syncPublisher(SearchRequestListener.TOPIC).failed(requestId);
    }

    @Override
    public void unauthorized(SearchInfo searchInfo) {
        UUID requestId = searchInfo.getRequestId();
        requests.remove(requestId);
        myProject.getMessageBus().syncPublisher(SearchRequestListener.TOPIC).failed(requestId);
    }

    @Override
    public void searchFailed(SearchInfo searchInfo, SamebugClientException error) {
        UUID requestId = searchInfo.getRequestId();
        requests.remove(requestId);
        myProject.getMessageBus().syncPublisher(SearchRequestListener.TOPIC).failed(requestId);
    }
}
