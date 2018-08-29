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
package com.samebug.clients.idea.controllers;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.util.messages.MessageBusConnection;
import com.samebug.clients.common.entities.search.DebugSessionInfo;
import com.samebug.clients.common.entities.search.RequestedSearch;
import com.samebug.clients.common.entities.search.SavedSearch;
import com.samebug.clients.common.entities.search.SearchInfo;
import com.samebug.clients.common.services.SearchRequestService;
import com.samebug.clients.common.services.SearchService;
import com.samebug.clients.http.entities.jsonapi.CreatedSearchResource;
import com.samebug.clients.http.entities.search.QueryInfo;
import com.samebug.clients.http.entities.search.StackTraceInfo;
import com.samebug.clients.http.exceptions.SamebugClientException;
import com.samebug.clients.idea.components.application.IdeaSamebugPlugin;
import com.samebug.clients.idea.search.SearchRequestListener;
import com.samebug.clients.idea.search.StackTraceMatcherListener;

public class ConsoleSearchController implements StackTraceMatcherListener {
    public ConsoleSearchController(MessageBusConnection connection) {
        connection.subscribe(StackTraceMatcherListener.TOPIC, this);
    }

    @Override
    public void stackTraceFound(final Project project, final DebugSessionInfo sessionInfo, final String stackTrace) {
        ApplicationManager.getApplication().executeOnPooledThread(new Runnable() {
            @Override
            public void run() {
                final SearchService searchService = IdeaSamebugPlugin.getInstance().searchService;
                final SearchRequestService searchRequestService = IdeaSamebugPlugin.getInstance().searchRequestService;
                final SearchInfo searchInfo = new SearchInfo(sessionInfo);

                RequestedSearch requestedSearch = searchRequestService.searchStart(searchInfo, stackTrace);
                if (!project.isDisposed()) project.getMessageBus().syncPublisher(SearchRequestListener.TOPIC).newSearchRequest(requestedSearch);

                try {
                    CreatedSearchResource result = searchService.search(stackTrace);
                    QueryInfo queryInfo = result.getData().getQueryInfo();
                    if (!(queryInfo instanceof StackTraceInfo)) {
                        // ignore text search
                        searchRequestService.searchFailed(searchInfo);
                        if (!project.isDisposed()) project.getMessageBus().syncPublisher(SearchRequestListener.TOPIC).failedSearch(searchInfo);
                    } else {
                        SavedSearch savedSearchRequest = searchRequestService.searchSucceeded(searchInfo, requestedSearch, result);
                        if (!project.isDisposed()) {
                            if (savedSearchRequest != null) project.getMessageBus().syncPublisher(SearchRequestListener.TOPIC).savedSearch(savedSearchRequest);
                            else project.getMessageBus().syncPublisher(SearchRequestListener.TOPIC).failedSearch(searchInfo);
                        }
                    }
                } catch (SamebugClientException e) {
                    searchRequestService.searchFailed(searchInfo);
                    if (!project.isDisposed()) project.getMessageBus().syncPublisher(SearchRequestListener.TOPIC).failedSearch(searchInfo);
                }
            }
        });
    }
}
