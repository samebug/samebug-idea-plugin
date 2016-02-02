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
package com.samebug.clients.idea.project;

import com.intellij.ide.BrowserUtil;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.samebug.clients.idea.application.IdeaSamebugPlugin;
import com.samebug.clients.idea.notification.NotificationActionListener;
import com.samebug.clients.idea.notification.SearchResultsNotification;
import com.samebug.clients.idea.project.autosearch.StackTraceSearch;
import com.samebug.clients.idea.resources.SamebugBundle;
import com.samebug.clients.search.api.SamebugClient;
import com.samebug.clients.search.api.entities.SearchResults;
import com.samebug.clients.search.api.exceptions.SamebugClientException;

class SearchResultNotifier implements StackTraceSearch.StackTraceSearchListener {
    public SearchResultNotifier(Project project) {
        this.project = project;
        project.getMessageBus().connect().subscribe(StackTraceSearch.StackTraceSearchListener.SEARCH_TOPIC, this);
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
        final SamebugClient client = IdeaSamebugPlugin.getClient();

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
        LOGGER.debug("Showing Samebug notification about search " + results.searchId);
        ApplicationManager.getApplication().invokeLater(new Runnable() {
            public void run() {
                notification.notify(project);
            }
        });
    }

    private final Project project;
    private final static Logger LOGGER = Logger.getInstance(SearchResultNotifier.class);
}
