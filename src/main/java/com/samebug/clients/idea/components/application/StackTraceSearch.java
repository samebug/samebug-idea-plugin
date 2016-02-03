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

import com.intellij.openapi.Disposable;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.ApplicationComponent;
import com.intellij.openapi.project.Project;
import com.intellij.util.messages.MessageBusConnection;
import com.intellij.util.messages.Topic;
import com.samebug.clients.idea.scanners.StackTraceMatcherFactory;
import com.samebug.clients.search.api.SamebugClient;
import com.samebug.clients.search.api.entities.SearchResults;
import com.samebug.clients.search.api.exceptions.SamebugClientException;
import com.samebug.clients.search.api.exceptions.SamebugTimeout;
import com.samebug.clients.search.api.exceptions.UserUnauthorized;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class StackTraceSearch implements ApplicationComponent, Disposable {
    @NotNull
    private static SamebugClient getClient() {
        return IdeaSamebugClient.getInstance();
    }

    @NotNull
    public static StackTraceSearch getInstance() {
        StackTraceSearch instance = ApplicationManager.getApplication().getComponent(StackTraceSearch.class);
        if (instance == null) {
            throw new Error("No Samebug IDEA search available");
        } else {
            return instance;
        }
    }


    public void search(final Project project, final String stacktrace) {
        ApplicationManager.getApplication().executeOnPooledThread(new Runnable() {
            @Override
            public void run() {
                StackTraceSearchListener listener = project.getMessageBus().syncPublisher(StackTraceSearchListener.SEARCH_TOPIC);
                String id = UUID.randomUUID().toString();
                listener.searchStart(id, stacktrace);
                try {
                    listener.searchSucceeded(id, getClient().searchSolutions(stacktrace));
                } catch (SamebugTimeout ignored) {
                    listener.timeout(id);
                } catch (UserUnauthorized ignored) {
                    listener.unauthorized(id);
                } catch (SamebugClientException e) {
                    listener.searchFailed(id, e);
                }
            }
        });
    }

    @Override
    public void initComponent() {
        MessageBusConnection messageBusConnection = ApplicationManager.getApplication().getMessageBus().connect(this);
        messageBusConnection.subscribe(
                StackTraceMatcherFactory.StackTraceMatcherListener.FOUND_TOPIC, new StackTraceMatcherFactory.StackTraceMatcherListener() {
            @Override
            public void stackTraceFound(Project project, String stackTrace) {
                search(project, stackTrace);
            }
        });
    }

    @Override
    public void disposeComponent() {

    }


    @NotNull
    @Override
    public String getComponentName() {
        return getClass().getSimpleName();
    }

    @Override
    public void dispose() {

    }

    public interface StackTraceSearchListener {
        Topic<StackTraceSearchListener> SEARCH_TOPIC = Topic.create("stacktrace search", StackTraceSearchListener.class);

        void searchStart(String id, String stackTrace);

        void searchSucceeded(String id, SearchResults results);

        void timeout(String id);

        void unauthorized(String id);

        void searchFailed(String id, SamebugClientException error);
    }
}
