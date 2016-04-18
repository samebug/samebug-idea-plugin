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
package com.samebug.clients.idea.components.application;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.ApplicationComponent;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.util.messages.MessageBusConnection;
import com.samebug.clients.idea.messages.StackTraceMatcherListener;
import com.samebug.clients.idea.messages.StackTraceSearchListener;
import com.samebug.clients.idea.tracking.Events;
import com.samebug.clients.search.api.entities.SearchResults;
import com.samebug.clients.search.api.entities.tracking.DebugSessionInfo;
import com.samebug.clients.search.api.entities.tracking.SearchInfo;
import com.samebug.clients.search.api.exceptions.SamebugClientException;
import com.samebug.clients.search.api.exceptions.SamebugTimeout;
import com.samebug.clients.search.api.exceptions.UserUnauthorized;
import org.jetbrains.annotations.NotNull;

public class StackTraceSearch implements ApplicationComponent, StackTraceMatcherListener {
    private MessageBusConnection messageBusConnection;
    private final static Logger LOGGER = Logger.getInstance(StackTraceSearch.class);

    // ApplicationComponent overrides
    @Override
    public void initComponent() {
        messageBusConnection = ApplicationManager.getApplication().getMessageBus().connect();
        messageBusConnection.subscribe(StackTraceMatcherListener.FOUND_TOPIC, this);
    }

    @Override
    public void disposeComponent() {
        messageBusConnection.disconnect();
    }

    @NotNull
    @Override
    public String getComponentName() {
        return getClass().getSimpleName();
    }

    // StackTraceMatcherListener overrides
    @Override
    public void stackTraceFound(final Project project, final DebugSessionInfo sessionInfo, final String stackTrace) {
        final IdeaClientService client = IdeaSamebugPlugin.getInstance().getClient();
        ApplicationManager.getApplication().executeOnPooledThread(new Runnable() {
            @Override
            public void run() {
                SearchInfo searchInfo = new SearchInfo(sessionInfo);
                // TODO I cannot see what stops 'Already disposed' errors here, but the docs say this is the intended usage.
                if (!project.isDisposed()) project.getMessageBus().syncPublisher(StackTraceSearchListener.SEARCH_TOPIC).searchStart(searchInfo, stackTrace);
                try {
                    SearchResults result = client.searchSolutions(stackTrace);
                    if (!project.isDisposed()) project.getMessageBus().syncPublisher(StackTraceSearchListener.SEARCH_TOPIC).searchSucceeded(searchInfo, result);
                    Tracking.projectTracking(project).trace(Events.searchSucceeded(searchInfo, result));
                } catch (SamebugTimeout ignored) {
                    if (!project.isDisposed()) project.getMessageBus().syncPublisher(StackTraceSearchListener.SEARCH_TOPIC).timeout(searchInfo);
                } catch (UserUnauthorized ignored) {
                    if (!project.isDisposed()) project.getMessageBus().syncPublisher(StackTraceSearchListener.SEARCH_TOPIC).unauthorized(searchInfo);
                } catch (SamebugClientException e) {
                    if (!project.isDisposed()) project.getMessageBus().syncPublisher(StackTraceSearchListener.SEARCH_TOPIC).searchFailed(searchInfo, e);
                }
            }
        });
    }
}
