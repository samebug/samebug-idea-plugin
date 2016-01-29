package com.samebug.clients.idea;

import com.intellij.execution.ui.RunContentManager;
import com.intellij.ide.BrowserUtil;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.util.messages.MessageBusConnection;
import com.samebug.clients.idea.adb.AndroidSdkUtils;
import com.samebug.clients.idea.intellij.autosearch.StackTraceSearch;
import com.samebug.clients.idea.intellij.autosearch.android.AndroidShellSolutionSearch;
import com.samebug.clients.idea.intellij.autosearch.console.ConsoleScannerStackTraceSearch;
import com.samebug.clients.idea.intellij.notification.NotificationActionListener;
import com.samebug.clients.idea.intellij.notification.SearchResultsNotification;
import com.samebug.clients.idea.messages.SamebugBundle;
import com.samebug.clients.rest.SamebugClient;
import com.samebug.clients.rest.entities.SearchResults;
import com.samebug.clients.rest.exceptions.SamebugClientException;
import org.jetbrains.annotations.NotNull;

public class SamebugProjectComponent implements ProjectComponent {

    private StackTraceSearch searchEngine;
    private ConsoleScannerStackTraceSearch consoleSearch;
    private AndroidShellSolutionSearch androidShellSolutionSearch;

    public SamebugProjectComponent(final Project project) {
        this.project = project;
    }

    /**
     * Subscribe a @see com.samebug.clients.idea.intellij.ProcessOutputScannerInstaller on the project message bus
     */
    @Override
    public void projectOpened() {
        initScanners(project);
    }


    @Override
    public void initComponent() {

    }

    private void initScanners(@NotNull Project project) {
        this.searchEngine = SamebugIdeaPlugin.getSearchEngine();
        this.consoleSearch = new ConsoleScannerStackTraceSearch(searchEngine, project, new StackTraceSearch.SearchResultListener() {
            @Override
            public void handleResults(SearchResults results) {
                if (results.totalSolutions>0) showNotificationPopup(results);
            }

            @Override
            public void handleException(SamebugClientException exception) {
                logger.error("Error in Samebug console search", exception);
            }
        });
        this.androidShellSolutionSearch = new AndroidShellSolutionSearch(searchEngine, new StackTraceSearch.SearchResultListener() {
            @Override
            public void handleResults(SearchResults results) {
                if (results.totalSolutions>0) showNotificationPopup(results);
            }

            @Override
            public void handleException(SamebugClientException exception) {
                logger.error("Error in Samebug console search", exception);
            }
        });

        MessageBusConnection messageBusConnection = project.getMessageBus().connect();
        messageBusConnection.subscribe(RunContentManager.TOPIC, androidShellSolutionSearch);
    }

    private void showNotificationPopup(final SearchResults results) {
        final SamebugClient client = SamebugIdeaPlugin.getClient();

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
        logger.debug("Showing Samebug notification about search " + results.searchId);
        ApplicationManager.getApplication().invokeLater(new Runnable() {
            public void run() {
                notification.notify(project);
            }
        });
    }

    @Override
    public void disposeComponent() {
    }

    @Override
    @NotNull
    public String getComponentName() {
        return getClass().getSimpleName();
    }


    @Override
    public void projectClosed() {
    }

    private final Project project;


    private final static Logger logger = Logger.getInstance(SamebugProjectComponent.class);
}

