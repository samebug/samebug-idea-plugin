/**
 * Copyright 2017 Samebug, Inc.
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
package com.samebug.clients.idea.controllers;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.util.messages.MessageBusConnection;
import com.samebug.clients.common.entities.search.RequestedSearch;
import com.samebug.clients.common.entities.search.SavedSearch;
import com.samebug.clients.common.entities.search.SearchInfo;
import com.samebug.clients.common.search.api.entities.SearchResults;
import com.samebug.clients.common.search.api.exceptions.SamebugClientException;
import com.samebug.clients.common.services.SearchRequestService;
import com.samebug.clients.common.services.SearchRequestStore;
import com.samebug.clients.idea.messages.StackTraceSearchListener;
import com.samebug.clients.idea.search.SearchRequestListener;

public final class SessionsController implements StackTraceSearchListener {
    final private static Logger LOGGER = Logger.getInstance(SessionsController.class);

    private final SearchRequestStore store;
    private final SearchRequestService service;

    public SessionsController(MessageBusConnection connection, SearchRequestService service, SearchRequestStore store) {
        this.store = store;
        this.service = service;

        connection.subscribe(StackTraceSearchListener.TOPIC, this);
    }

    @Override
    public void searchStart(Project project, SearchInfo searchInfo, String stackTrace) {
        RequestedSearch requestedSearch = service.searchStart(searchInfo, stackTrace);
        project.getMessageBus().syncPublisher(SearchRequestListener.TOPIC).newSearchRequest(requestedSearch);
    }

    @Override
    public void searchSucceeded(Project project, SearchInfo searchInfo, SearchResults result) {
        SavedSearch savedSearchRequest = service.searchSucceeded(searchInfo, result);
        if (savedSearchRequest != null) {
            project.getMessageBus().syncPublisher(SearchRequestListener.TOPIC).savedSearch(savedSearchRequest);
        } else {
            project.getMessageBus().syncPublisher(SearchRequestListener.TOPIC).failedSearch(searchInfo);
        }
    }

    @Override
    public void searchFailed(Project project, SearchInfo searchInfo, SamebugClientException error) {
        service.searchFailed(searchInfo);
        project.getMessageBus().syncPublisher(SearchRequestListener.TOPIC).failedSearch(searchInfo);
    }

    @Override
    public void searchError(Project project, SearchInfo searchInfo, Throwable error) {
        service.searchFailed(searchInfo);
        project.getMessageBus().syncPublisher(SearchRequestListener.TOPIC).failedSearch(searchInfo);
        LOGGER.warn("Unexpected error happened during search", error);
    }
}
