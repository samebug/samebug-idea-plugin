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
package com.samebug.clients.idea.components.project;

import com.intellij.notification.NotificationType;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.AbstractProjectComponent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.util.messages.MessageBusConnection;
import com.samebug.clients.idea.messages.BatchStackTraceSearchListener;
import com.samebug.clients.idea.notification.NotificationActionListener;
import com.samebug.clients.idea.notification.SamebugNotification;
import com.samebug.clients.idea.resources.SamebugBundle;
import com.samebug.clients.search.api.entities.SearchResults;
import org.jetbrains.annotations.Nullable;

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
        String message = SamebugBundle.message("samebug.notification.searchresults.message", results.size());
        final SearchResultsNotification notification = new SearchResultsNotification(
                message, new NotificationActionListener() {
            @Override
            public void actionActivated(String action) {
                if (SearchResultsNotification.SHOW.equals(action)) {
                    ToolWindowManager.getInstance(myProject).getToolWindow("Samebug").show(null);
                }
            }
        });

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

    private final int NOTIFICATION_EXPIRATION_DELAY = 10000;
    private MessageBusConnection messageBusConnection;

}

class SearchResultsNotification extends SamebugNotification {
    public SearchResultsNotification(String message, @Nullable NotificationActionListener actionListener) {
        super(SamebugBundle.message("samebug.notification.searchresults.title"), message, NotificationType.INFORMATION, actionListener);

        whenExpired(new Runnable() {
            @Override
            public void run() {
                hideBalloon();
            }
        });
    }


    public final static String SHOW = "#show";
}