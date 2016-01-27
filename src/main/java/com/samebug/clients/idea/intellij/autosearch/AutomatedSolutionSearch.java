package com.samebug.clients.idea.intellij.autosearch;

import com.intellij.ide.BrowserUtil;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.samebug.clients.idea.SamebugIdeaPlugin;
import com.samebug.clients.idea.SamebugProjectComponent;
import com.samebug.clients.api.LogScannerFactory;
import com.samebug.clients.api.StackTraceListener;
import com.samebug.clients.rest.SamebugClient;
import com.samebug.clients.rest.entities.SearchResults;
import com.samebug.clients.rest.exceptions.SamebugClientException;
import com.samebug.clients.idea.intellij.notification.NotificationActionListener;
import com.samebug.clients.idea.intellij.notification.SearchResultsNotification;
import com.samebug.clients.idea.messages.SamebugBundle;
import org.jetbrains.annotations.Nullable;

public class AutomatedSolutionSearch implements StackTraceListener {
    protected final SamebugClient client;
    protected final LogScannerFactory logScannerFactory;
    @Nullable
    private final Project project;

    public AutomatedSolutionSearch() {
        this(null);
    }

    public AutomatedSolutionSearch(@Nullable Project project) {
        this.project = project;
        client = SamebugIdeaPlugin.getClient();
        logScannerFactory = new StackTraceMatcherFactory(this);
    }

    public void stacktraceFound(final String stacktrace) {
        ApplicationManager.getApplication().invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    final SearchResults results = client.searchSolutions(stacktrace);
                    if (results.totalSolutions > 0) {
                        showNotificationPopup(results, project);
                    }
                } catch (SamebugClientException e) {
                    logger.error("Unable to search solutions", e);
                    logger.info("Stacktrace\n: " + stacktrace);
                }
            }
        });
    }

    private void showNotificationPopup(final SearchResults results, @Nullable final Project project) {
        String message = SamebugBundle.message("samebug.notification.searchresults.message", results.totalSolutions);
        final SearchResultsNotification notification = new SearchResultsNotification(
                message, new NotificationActionListener() {
            @Override
            public void actionActivated(String action) {
                if (SearchResultsNotification.SHOW.equals(action)) {
                    try {
                        BrowserUtil.browse(client.getSearchUrl(Integer.parseInt(results.searchId)));
                    } catch (SamebugClientException e) {
                        logger.error("Unable to open results in browser for search " + results.searchId, e);
                    }
                }
            }
        });
        notification.notify(project);
    }

    private final static Logger logger = Logger.getInstance(SamebugProjectComponent.class);

}
