/**
 * Copyright 2016 Samebug, Inc.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.samebug.clients.common.services;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.util.messages.MessageBusConnection;
import com.samebug.clients.common.entities.search.Requested;
import com.samebug.clients.common.entities.search.Saved;
import com.samebug.clients.common.entities.search.SearchRequest;
import com.samebug.clients.idea.messages.console.SearchRequestListener;
import com.samebug.clients.idea.messages.model.StackTraceSearchListener;
import com.samebug.clients.search.api.entities.SearchResults;
import com.samebug.clients.search.api.entities.tracking.SearchInfo;
import com.samebug.clients.search.api.exceptions.SamebugClientException;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class RequestService implements StackTraceSearchListener {
    private final Logger LOGGER = Logger.getInstance(RequestService.class);
    private final Project myProject;
    private final Map<UUID, SearchRequest> requests;

    public RequestService(Project project) {
        myProject = project;
        requests = new ConcurrentHashMap<UUID, SearchRequest>();
        MessageBusConnection projectConnection = project.getMessageBus().connect(project);
        projectConnection.subscribe(StackTraceSearchListener.TOPIC, this);
    }

    @Nullable
    public SearchRequest getRequest(UUID requestId) {
        return requests.get(requestId);
    }

    public Map<UUID, SearchRequest> getRequests() {
        return requests;
    }

    public void removeRequest(UUID requestId) {
        requests.remove(requestId);
    }

    @Override
    public void searchStart(SearchInfo searchInfo, String stackTrace) {
        Requested request = new Requested(stackTrace);
        requests.put(searchInfo.getRequestId(), request);
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
        LOGGER.trace("timeout " + searchInfo);
        UUID requestId = searchInfo.getRequestId();
        requests.remove(requestId);
        myProject.getMessageBus().syncPublisher(SearchRequestListener.TOPIC).failed(requestId);
    }

    @Override
    public void unauthorized(SearchInfo searchInfo) {
        LOGGER.trace("unauthorized " + searchInfo);
        UUID requestId = searchInfo.getRequestId();
        requests.remove(requestId);
        myProject.getMessageBus().syncPublisher(SearchRequestListener.TOPIC).failed(requestId);
    }

    @Override
    public void searchFailed(SearchInfo searchInfo, SamebugClientException error) {
        LOGGER.trace("failed " + searchInfo);
        UUID requestId = searchInfo.getRequestId();
        requests.remove(requestId);
        myProject.getMessageBus().syncPublisher(SearchRequestListener.TOPIC).failed(requestId);
    }
}
