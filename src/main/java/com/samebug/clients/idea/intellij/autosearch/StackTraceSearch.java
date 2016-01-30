package com.samebug.clients.idea.intellij.autosearch;

import com.intellij.ide.BrowserUtil;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.samebug.clients.idea.SamebugIdeaPlugin;
import com.samebug.clients.idea.intellij.notification.NotificationActionListener;
import com.samebug.clients.idea.intellij.notification.SearchResultsNotification;
import com.samebug.clients.idea.messages.SamebugBundle;
import com.samebug.clients.rest.SamebugClient;
import com.samebug.clients.rest.entities.SearchResults;
import com.samebug.clients.rest.exceptions.SamebugClientException;
import com.samebug.clients.rest.exceptions.SamebugTimeout;
import com.samebug.clients.rest.exceptions.UserUnauthorized;

public class StackTraceSearch {
    protected final SamebugClient client;

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
                } catch (SamebugTimeout e) {

                } catch (UserUnauthorized ignored) {

                } catch (SamebugClientException e) {
                    resultHandler.handleException(e);
                }
            }
        });
    }


    private final static Logger logger = Logger.getInstance(StackTraceSearch.class);

    public interface SearchResultListener {
        void handleResults(SearchResults results);

        void handleException(SamebugClientException exception);
    }
}
