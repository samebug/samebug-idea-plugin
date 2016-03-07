package com.samebug.clients.idea.ui.controller;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.samebug.clients.idea.components.application.IdeaSamebugPlugin;
import com.samebug.clients.idea.messages.BatchStackTraceSearchListener;
import com.samebug.clients.idea.messages.ConnectionStatusListener;
import com.samebug.clients.idea.resources.SamebugBundle;
import com.samebug.clients.idea.resources.SamebugIcons;
import com.samebug.clients.idea.ui.views.HistoryTabView;
import com.samebug.clients.search.api.SamebugClient;
import com.samebug.clients.search.api.entities.GroupedExceptionSearch;
import com.samebug.clients.search.api.entities.GroupedHistory;
import com.samebug.clients.search.api.entities.SearchResults;
import com.samebug.clients.search.api.exceptions.SamebugClientException;

import javax.swing.*;
import javax.swing.text.DefaultCaret;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by poroszd on 3/4/16.
 */
public class HistoryTabController {
    final private Project project;
    final private static Logger LOGGER = Logger.getInstance(HistoryTabController.class);
    final private HistoryTabView view;
    final private HistoryReloader historyReloader;
    final private ConnectionStatusUpdater statusUpdater;
    final private List<SearchGroupCardController> searchGroups;

    public HistoryTabController(Project project) {
        this.project = project;
        view = new HistoryTabView();
        historyReloader = new HistoryReloader();
        statusUpdater = new ConnectionStatusUpdater();
        searchGroups = new ArrayList<SearchGroupCardController>();
    }

    public HistoryReloader getHistoryReloader() {
        return historyReloader;
    }

    public ConnectionStatusUpdater getStatusUpdater() {
        return statusUpdater;
    }

    public JPanel getControlPanel() {
        return view.controlPanel;
    }

    public void loadHistory() {
        final IdeaSamebugPlugin plugin = IdeaSamebugPlugin.getInstance();
        if (plugin.isInitialized()) {
            ApplicationManager.getApplication().executeOnPooledThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        emptyHistoryPane();
                        final GroupedHistory history = plugin.getClient().getSearchHistory();
                        refreshHistoryPane(history);
                    } catch (SamebugClientException e1) {
                        LOGGER.warn("Failed to retrieve history", e1);
                    }
                }
            });
        }
    }

    private void emptyHistoryPane() {
        ApplicationManager.getApplication().invokeLater(new Runnable() {
            public void run() {
                view.contentPanel.removeAll();
                view.controlPanel.invalidate();
            }
        });
    }

    private void refreshHistoryPane(final GroupedHistory history) {
        ApplicationManager.getApplication().invokeLater(new Runnable() {
            public void run() {
                searchGroups.clear();
                for (GroupedExceptionSearch group : history.searchGroups) {
                    SearchGroupCardController c = new SearchGroupCardController();
                    searchGroups.add((c));
                    view.contentPanel.add(c.getControlPanel());
                    c.show(group);
                }
            }
        });
    }

    class ConnectionStatusUpdater implements ConnectionStatusListener {
        @Override
        public void startRequest() {
            ApplicationManager.getApplication().invokeLater(new Runnable() {
                @Override
                public void run() {
                    view.statusIcon.setIcon(SamebugIcons.linkActive);
                    view.statusIcon.setToolTipText(SamebugBundle.message("samebug.toolwindow.history.connectionStatus.description.loading"));
                    view.statusIcon.invalidate();
                }
            });
        }

        @Override
        public void finishRequest(final boolean isConnected) {
            ApplicationManager.getApplication().invokeLater(new Runnable() {
                @Override
                public void run() {
                    if (IdeaSamebugPlugin.getInstance().getClient().getNumberOfActiveRequests() == 0) {
                        if (isConnected) {
                            view.statusIcon.setIcon(null);
                            view.statusIcon.setToolTipText(null);
                        } else {
                            view.statusIcon.setIcon(SamebugIcons.linkError);
                            view.statusIcon.setToolTipText(SamebugBundle.message("samebug.toolwindow.history.connectionStatus.description.notConnected", SamebugClient.root));
                        }
                        view.statusIcon.invalidate();
                    }
                }
            });
        }

        @Override
        public void authorizationChange(final boolean isAuthorized) {
        }

    }

    class HistoryReloader implements BatchStackTraceSearchListener {
        @Override
        public void batchStart() {

        }

        @Override
        public void batchFinished(java.util.List<SearchResults> results, int failed) {
            loadHistory();
        }
    }
}
