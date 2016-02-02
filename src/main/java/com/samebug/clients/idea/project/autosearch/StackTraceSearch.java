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
package com.samebug.clients.idea.project.autosearch;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.util.messages.Topic;
import com.samebug.clients.rest.SamebugClient;
import com.samebug.clients.rest.entities.SearchResults;
import com.samebug.clients.rest.exceptions.SamebugClientException;
import com.samebug.clients.rest.exceptions.SamebugTimeout;
import com.samebug.clients.rest.exceptions.UserUnauthorized;

import java.util.UUID;

public class StackTraceSearch {
    private final Project project;
    private final SamebugClient client;

    public StackTraceSearch(Project project, SamebugClient client) {
        this.project = project;
        this.client = client;
    }

    public void search(final String stacktrace) {
        ApplicationManager.getApplication().executeOnPooledThread(new Runnable() {
            @Override
            public void run() {
                StackTraceSearchListener listener = project.getMessageBus().syncPublisher(StackTraceSearchListener.SEARCH_TOPIC);
                String id = UUID.randomUUID().toString();
                listener.searchStart(id, stacktrace);
                try {
                    listener.searchSucceeded(id, client.searchSolutions(stacktrace));
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

    public interface StackTraceSearchListener {
        Topic<StackTraceSearchListener> SEARCH_TOPIC = Topic.create("stacktrace search", StackTraceSearchListener.class);

        void searchStart(String id, String stackTrace);

        void searchSucceeded(String id, SearchResults results);

        void timeout(String id);

        void unauthorized(String id);

        void searchFailed(String id, SamebugClientException error);
    }
}
