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
package com.samebug.clients.idea.intellij.autosearch;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.samebug.clients.rest.SamebugClient;
import com.samebug.clients.rest.entities.SearchResults;
import com.samebug.clients.rest.exceptions.SamebugClientException;
import com.samebug.clients.rest.exceptions.SamebugTimeout;
import com.samebug.clients.rest.exceptions.UserUnauthorized;

public class StackTraceSearch {
    private final SamebugClient client;

    public StackTraceSearch(SamebugClient client) {
        this.client = client;
    }

    public void search(final String stacktrace, final SearchResultListener resultHandler) {
        ApplicationManager.getApplication().executeOnPooledThread(new Runnable() {
            @Override
            public void run() {
                final SearchResults results;
                try {
                    results = client.searchSolutions(stacktrace);
                    resultHandler.handleResults(results);
                } catch (SamebugTimeout ignored) {

                } catch (UserUnauthorized ignored) {

                } catch (SamebugClientException e) {
                    resultHandler.handleException(e);
                }
            }
        });
    }


    private final static Logger LOGGER = Logger.getInstance(StackTraceSearch.class);

    public interface SearchResultListener {
        void handleResults(SearchResults results);

        void handleException(SamebugClientException exception);
    }
}
