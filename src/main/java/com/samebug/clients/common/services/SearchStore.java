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
package com.samebug.clients.common.services;

import com.intellij.openapi.project.Project;
import com.intellij.util.messages.MessageBusConnection;
import com.samebug.clients.idea.messages.console.SearchFinishedListener;
import com.samebug.clients.idea.messages.model.StackTraceSearchListener;
import com.samebug.clients.search.api.entities.SearchResults;
import com.samebug.clients.search.api.entities.tracking.SearchInfo;
import com.samebug.clients.search.api.exceptions.SamebugClientException;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class SearchStore implements StackTraceSearchListener {
    private final Project myProject;
    private final Map<UUID, String> traces;
    private final Map<UUID, String> ongoingRequests;
    private final Map<UUID, SearchResults> results;

    public SearchStore(Project project) {
        myProject = project;
        traces = new ConcurrentHashMap<UUID, String>();
        ongoingRequests = new ConcurrentHashMap<UUID, String>();
        results = new ConcurrentHashMap<UUID, SearchResults>();
        MessageBusConnection projectConnection = project.getMessageBus().connect(project);
        projectConnection.subscribe(StackTraceSearchListener.TOPIC, this);
    }

    public Map<UUID, String> getTraces() {
        return traces;
    }

    @Nullable
    public SearchResults getResult(UUID requestId) {
        return results.get(requestId);
    }

    public void removeRequest(UUID requestId) {
        traces.remove(requestId);
        results.remove(requestId);
    }

    @Override
    public void searchStart(SearchInfo searchInfo, String stackTrace) {
        ongoingRequests.put(searchInfo.getRequestId(), stackTrace);
    }

    @Override
    public void searchSucceeded(SearchInfo searchInfo, SearchResults result) {
        UUID requestId = searchInfo.getRequestId();
        results.put(requestId, result);
        if (result.getStackTraceId() != null) {
            String trace = ongoingRequests.get(requestId);
            if (result.getFirstLine() != null) {
                int lineOffset = result.getFirstLine();
                for (int i = 0; i < lineOffset; ++i) {
                    int nextNL = trace.indexOf('\n');
                    trace = trace.substring(nextNL + 1);
                }
            }
            traces.put(requestId, trace);
        }
        ongoingRequests.remove(requestId);
        myProject.getMessageBus().syncPublisher(SearchFinishedListener.TOPIC).finishedProcessing();
    }

    @Override
    public void timeout(SearchInfo searchInfo) {
        traces.remove(searchInfo.getRequestId());
    }

    @Override
    public void unauthorized(SearchInfo searchInfo) {
        traces.remove(searchInfo.getRequestId());
    }

    @Override
    public void searchFailed(SearchInfo searchInfo, SamebugClientException error) {
        traces.remove(searchInfo.getRequestId());
    }
}
