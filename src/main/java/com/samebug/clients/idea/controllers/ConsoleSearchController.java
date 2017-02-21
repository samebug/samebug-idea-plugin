/**
 * Copyright 2017 Samebug, Inc.
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
package com.samebug.clients.idea.controllers;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.util.messages.MessageBusConnection;
import com.samebug.clients.common.search.api.entities.SearchResults;
import com.samebug.clients.common.entities.search.DebugSessionInfo;
import com.samebug.clients.common.entities.search.SearchInfo;
import com.samebug.clients.common.search.api.exceptions.SamebugClientException;
import com.samebug.clients.common.services.SearchService;
import com.samebug.clients.idea.components.application.IdeaSamebugPlugin;
import com.samebug.clients.idea.components.application.Tracking;
import com.samebug.clients.idea.messages.StackTraceSearchListener;
import com.samebug.clients.idea.search.StackTraceMatcherListener;
import com.samebug.clients.idea.tracking.Events;

public class ConsoleSearchController implements StackTraceMatcherListener {
    private final static Logger LOGGER = Logger.getInstance(ConsoleSearchController.class);

    public ConsoleSearchController(MessageBusConnection connection) {
        connection.subscribe(StackTraceMatcherListener.TOPIC, this);
    }

    @Override
    public void stackTraceFound(final Project project, final DebugSessionInfo sessionInfo, final String stackTrace) {
        ApplicationManager.getApplication().executeOnPooledThread(new Runnable() {
            @Override
            public void run() {
                final SearchService service = IdeaSamebugPlugin.getInstance().getSearchService();
                final SearchInfo searchInfo = new SearchInfo(sessionInfo);

                if (!project.isDisposed()) project.getMessageBus().syncPublisher(StackTraceSearchListener.TOPIC).searchStart(project, searchInfo, stackTrace);
                try {
                    SearchResults result = service.search(stackTrace);
                    if (!project.isDisposed()) project.getMessageBus().syncPublisher(StackTraceSearchListener.TOPIC).searchSucceeded(project, searchInfo, result);
                    Tracking.projectTracking(project).trace(Events.searchSucceeded(searchInfo, result));
                } catch (SamebugClientException e) {
                    if (!project.isDisposed()) project.getMessageBus().syncPublisher(StackTraceSearchListener.TOPIC).searchFailed(project, searchInfo, e);
                } catch (Throwable e) {
                    if (!project.isDisposed()) project.getMessageBus().syncPublisher(StackTraceSearchListener.TOPIC).searchError(project, searchInfo, e);
                }
            }
        });
    }
}
