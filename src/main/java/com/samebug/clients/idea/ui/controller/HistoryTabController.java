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
package com.samebug.clients.idea.ui.controller;

import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.samebug.clients.idea.actions.ShowOld;
import com.samebug.clients.idea.actions.ShowZeroSolution;
import com.samebug.clients.idea.components.application.IdeaClientService;
import com.samebug.clients.idea.components.application.IdeaSamebugPlugin;
import com.samebug.clients.idea.messages.BatchStackTraceSearchListener;
import com.samebug.clients.idea.messages.ConnectionStatusListener;
import com.samebug.clients.idea.messages.HistoryListener;
import com.samebug.clients.idea.resources.SamebugBundle;
import com.samebug.clients.idea.resources.SamebugIcons;
import com.samebug.clients.idea.ui.layout.EmptyWarningPanel;
import com.samebug.clients.idea.ui.views.HistoryTabView;
import com.samebug.clients.search.api.SamebugClient;
import com.samebug.clients.search.api.entities.GroupedExceptionSearch;
import com.samebug.clients.search.api.entities.GroupedHistory;
import com.samebug.clients.search.api.entities.SearchResults;
import com.samebug.clients.search.api.exceptions.SamebugClientException;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by poroszd on 3/4/16.
 */
public class HistoryTabController {
    final private Project project;
    final private static Logger LOGGER = Logger.getInstance(HistoryTabController.class);
    final private HistoryTabView view;
    @Nullable
    private GroupedHistory model;
    final private List<SearchGroupCardController> searchGroups;

    final private HistoryReloader historyReloader;
    final private ConnectionStatusUpdater statusUpdater;
    final private HistoryUpdater historyUpdater;


    public HistoryTabController(Project project) {
        this.project = project;
        view = new HistoryTabView();
        searchGroups = new ArrayList<SearchGroupCardController>();

        historyReloader = new HistoryReloader();
        statusUpdater = new ConnectionStatusUpdater();
        historyUpdater = new HistoryUpdater();
    }

    public HistoryReloader getHistoryReloader() {
        return historyReloader;
    }

    public ConnectionStatusUpdater getStatusUpdater() {
        return statusUpdater;
    }

    public HistoryUpdater getHistoryUpdater() {
        return historyUpdater;
    }

    public JPanel getControlPanel() {
        return view.controlPanel;
    }

    public void loadHistory() {
        ApplicationManager.getApplication().executeOnPooledThread(new Runnable() {
            @Override
            public void run() {
                emptyHistoryPane();
                model = null;
                try {
                    final IdeaSamebugPlugin plugin = IdeaSamebugPlugin.getInstance();
                    model = plugin.getClient().getSearchHistory();
                } catch (SamebugClientException e1) {
                    LOGGER.warn("Failed to retrieve history", e1);
                }
                refreshHistoryPane();
            }
        });
    }

    private void emptyHistoryPane() {
        ApplicationManager.getApplication().invokeLater(new Runnable() {
            public void run() {
                view.contentPanel.removeAll();
            }
        });
    }

    private void refreshHistoryPane() {
        ApplicationManager.getApplication().invokeLater(new Runnable() {
            public void run() {
                view.contentPanel.removeAll();
                searchGroups.clear();
                if (model != null) {
                    boolean showOldSearches = ((ShowOld) ActionManager.getInstance().getAction("Samebug.ShowOld")).isSelected(null);
                    boolean showSearchesWithZeroSolution = ((ShowZeroSolution) ActionManager.getInstance().getAction("Samebug.ShowZeroSolution")).isSelected(null);
                    Date now = new Date();
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(now);
                    cal.add(Calendar.DAY_OF_YEAR, -1);
                    Date oneDayBefore = cal.getTime();
                    for (GroupedExceptionSearch group : model.searchGroups) {
                        if (!showSearchesWithZeroSolution && group.numberOfSolutions == 0) {
                            // filtered because there is no solution for it
                        } else if (!showOldSearches && group.firstSeenSimilar.before(oneDayBefore)) {
                            // filtered because it is old
                        } else {
                            SearchGroupCardController c = new SearchGroupCardController(group, project);
                            searchGroups.add(c);
                            view.contentPanel.add(c.getControlPanel());
                        }
                    }
                    if (searchGroups.isEmpty()) {
                        EmptyWarningPanel panel = new EmptyWarningPanel();
                        if (model.searchGroups.isEmpty()) {
                            panel.label.setText(SamebugBundle.message("samebug.toolwindow.history.content.noSearches"));
                        } else {
                            panel.label.setText(SamebugBundle.message("samebug.toolwindow.history.content.noVisibleSearches", SamebugIcons.lightbulbUrl, SamebugIcons.calendarUrl));
                        }
                        view.contentPanel.add(panel.controlPanel);
                    }
                } else {
                    IdeaClientService connectionService = IdeaSamebugPlugin.getInstance().getClient();
                    EmptyWarningPanel panel = new EmptyWarningPanel();
                    if (connectionService.isConnected() && !connectionService.isAuthenticated()) {
                        panel.label.setText(SamebugBundle.message("samebug.toolwindow.history.content.notLoggedIn", SamebugIcons.cogwheelTodoUrl));
                    } else {
                        panel.label.setText(SamebugBundle.message("samebug.toolwindow.history.content.notConnected", SamebugClient.root));
                    }
                    view.contentPanel.add(panel.controlPanel);
                }
                view.controlPanel.invalidate();
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
        public void authenticationChange(final boolean isAuthenticated) {
            loadHistory();
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

    class HistoryUpdater implements HistoryListener {

        @Override
        public void reload() {
            loadHistory();
        }

        @Override
        public void toggleShowSearchedWithZeroSolution(boolean enabled) {
            refreshHistoryPane();
        }

        @Override
        public void toggleShowOldSearches(boolean enabled) {
            refreshHistoryPane();
        }
    }
}
