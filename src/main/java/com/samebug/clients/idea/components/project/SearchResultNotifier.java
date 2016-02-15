/**
 * Copyright 2016 Samebug, Inc.
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.samebug.clients.idea.components.project;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.AbstractProjectComponent;
import com.intellij.openapi.project.Project;
import com.intellij.util.messages.MessageBusConnection;
import com.samebug.clients.idea.messages.BatchStackTraceSearchListener;
import com.samebug.clients.idea.notification.SearchResultsNotification;
import com.samebug.clients.search.api.entities.SearchResults;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

class SearchResultNotifier extends AbstractProjectComponent implements BatchStackTraceSearchListener {
    public SearchResultNotifier(Project project) {
        super(project);
    }

    @Override
    public void projectOpened() {
        super.projectOpened();
        messageBusConnection = myProject.getMessageBus().connect();
        messageBusConnection.subscribe(BatchStackTraceSearchListener.BATCH_SEARCH_TOPIC, this);
    }

    @Override
    public void projectClosed() {
        messageBusConnection.disconnect();
    }

    @Override
    public void batchStart() {

    }

    @Override
    public void batchFinished(List<SearchResults> results, int failed) {
        showNotification(results);
    }

    private void showNotification(final List<SearchResults> results) {
        final SearchResultsNotification notification = new SearchResultsNotification(myProject, results.size());

        final Timer timer = new Timer(NOTIFICATION_EXPIRATION_DELAY, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                notification.expire();
            }
        });

        ApplicationManager.getApplication().invokeLater(new Runnable() {
            public void run() {
                notification.notify(myProject);
                timer.start();
            }
        });
    }

    private final static int NOTIFICATION_EXPIRATION_DELAY = 10000;
    private MessageBusConnection messageBusConnection;

}
