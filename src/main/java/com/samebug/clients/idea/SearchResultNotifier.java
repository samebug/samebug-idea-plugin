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
package com.samebug.clients.idea;

import com.intellij.ide.BrowserUtil;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.samebug.clients.idea.intellij.autosearch.StackTraceSearch;
import com.samebug.clients.idea.intellij.notification.NotificationActionListener;
import com.samebug.clients.idea.intellij.notification.SearchResultsNotification;
import com.samebug.clients.idea.messages.SamebugBundle;
import com.samebug.clients.rest.SamebugClient;
import com.samebug.clients.rest.entities.SearchResults;
import com.samebug.clients.rest.exceptions.SamebugClientException;

class SearchResultNotifier implements StackTraceSearch.SearchResultListener {
    public SearchResultNotifier(Project project) {
        this.project = project;
    }

    @Override
    public void handleResults(SearchResults results) {
        if (results.totalSolutions > 0) showNotificationPopup(results);
    }

    @Override
    public void handleException(SamebugClientException exception) {
        LOGGER.error("Error in Samebug console search", exception);
    }

    private void showNotificationPopup(final SearchResults results) {
        final SamebugClient client = SamebugIdeaPlugin.getClient();

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
