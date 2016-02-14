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
import com.intellij.openapi.project.Project;
import com.intellij.util.messages.MessageBusConnection;
import com.samebug.clients.idea.messages.StackTraceMatcherListener;
import com.samebug.clients.idea.messages.StackTraceSearchListener;
import com.samebug.clients.search.api.SamebugClient;
import com.samebug.clients.search.api.exceptions.SamebugClientException;
import com.samebug.clients.search.api.exceptions.SamebugTimeout;
import com.samebug.clients.search.api.exceptions.UserUnauthorized;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class StackTraceSearch implements ApplicationComponent, StackTraceMatcherListener {
    private MessageBusConnection messageBusConnection;

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
    public void stackTraceFound(final Project project, final String stackTrace) {
        final SamebugClient client = IdeaSamebugPlugin.getInstance().getClient();
        ApplicationManager.getApplication().executeOnPooledThread(new Runnable() {
            @Override
            public void run() {
                StackTraceSearchListener listener = project.getMessageBus().syncPublisher(StackTraceSearchListener.SEARCH_TOPIC);
                String id = UUID.randomUUID().toString();
                listener.searchStart(id, stackTrace);
                try {
                    listener.searchSucceeded(id, client.searchSolutions(stackTrace));
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
}
