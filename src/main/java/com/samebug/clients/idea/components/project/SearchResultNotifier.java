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

import com.intellij.ide.BrowserUtil;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.AbstractProjectComponent;
import com.intellij.openapi.project.Project;
import com.intellij.util.messages.MessageBusConnection;
import com.samebug.clients.idea.components.application.IdeaSamebugClient;
import com.samebug.clients.idea.notification.NotificationActionListener;
import com.samebug.clients.idea.notification.SamebugNotification;
import com.samebug.clients.idea.resources.SamebugBundle;
import com.samebug.clients.search.api.SamebugClient;
import com.samebug.clients.search.api.entities.SearchResults;
import com.samebug.clients.search.api.exceptions.SamebugClientException;
import com.samebug.clients.search.api.messages.StackTraceSearchListener;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

class SearchResultNotifier extends AbstractProjectComponent implements StackTraceSearchListener {
    public SearchResultNotifier(Project project) {
        super(project);
    }

    @Override
    public void projectOpened() {
        super.projectOpened();
        messageBusConnection = myProject.getMessageBus().connect();
        messageBusConnection.subscribe(StackTraceSearchListener.SEARCH_TOPIC, this);
    }

    @Override
    public void projectClosed() {
        messageBusConnection.disconnect();
    }

    @Override
    public void searchStart(String id, String stackTrace) {

    }

    @Override
    public void searchSucceeded(String id, SearchResults results) {
        if (results.totalSolutions > 0) showNotificationPopup(results);
    }

    @Override
    public void timeout(String id) {

    }

    @Override
    public void unauthorized(String id) {

    }

    @Override
    public void searchFailed(String id, SamebugClientException error) {
    }

    private void showNotificationPopup(final SearchResults results) {
        final SamebugClient client = IdeaSamebugClient.getInstance();

        String message = SamebugBundle.message("samebug.notification.searchresults.message", results.totalSolutions);
        final SearchResultsNotification notification = new SearchResultsNotification(
                message, new NotificationActionListener() {
            @Override
            public void actionActivated(String action) {
                if (SearchResultsNotification.SHOW.equals(action)) {
                    BrowserUtil.browse(client.getSearchUrl(Integer.parseInt(results.searchId)));
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