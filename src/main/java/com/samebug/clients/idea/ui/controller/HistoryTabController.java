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

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.samebug.clients.idea.components.application.ApplicationSettings;
import com.samebug.clients.idea.components.application.IdeaClientService;
import com.samebug.clients.idea.components.application.IdeaSamebugPlugin;
import com.samebug.clients.idea.messages.ConnectionStatusListener;
import com.samebug.clients.idea.messages.HistoryListener;
import com.samebug.clients.idea.resources.SamebugBundle;
import com.samebug.clients.idea.resources.SamebugIcons;
import com.samebug.clients.idea.ui.layout.EmptyWarningPanel;
import com.samebug.clients.idea.ui.views.HistoryTabView;
import com.samebug.clients.idea.ui.views.SearchGroupCardView;
import com.samebug.clients.search.api.SamebugClient;
import com.samebug.clients.search.api.entities.GroupedExceptionSearch;
import com.samebug.clients.search.api.entities.GroupedHistory;
import com.samebug.clients.search.api.exceptions.SamebugClientException;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
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
    final private List<SearchGroupCardView> searchGroups;

    private boolean showZeroSolutionSearches;
    private boolean showRecurringSearches;

    final private ConnectionStatusUpdater statusUpdater;
    final private HistoryUpdater historyUpdater;


    public HistoryTabController(Project project) {
        this.project = project;
        view = new HistoryTabView();
        searchGroups = new ArrayList<SearchGroupCardView>();
        ApplicationSettings applicationSettings = IdeaSamebugPlugin.getInstance().getState();
        if (applicationSettings != null) {
            showZeroSolutionSearches = applicationSettings.isTutorialShowZeroSolutionSearches();
            showRecurringSearches = applicationSettings.isTutorialShowZeroSolutionSearches();
        }
        statusUpdater = new ConnectionStatusUpdater();
        historyUpdater = new HistoryUpdater();
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
                IdeaClientService connectionService = IdeaSamebugPlugin.getInstance().getClient();
                view.contentPanel.removeAll();
                searchGroups.clear();
                if (!connectionService.isConnected()) {
                    EmptyWarningPanel panel = new EmptyWarningPanel();
                    panel.label.setText(SamebugBundle.message("samebug.toolwindow.history.content.notConnected", SamebugClient.root));
                    view.contentPanel.add(panel.controlPanel);
                } else if (connectionService.isConnected() && !connectionService.isAuthenticated()) {
                    EmptyWarningPanel panel = new EmptyWarningPanel();
                    panel.label.setText(SamebugBundle.message("samebug.toolwindow.history.content.notLoggedIn", SamebugIcons.cogwheelTodoUrl));
                    view.contentPanel.add(panel.controlPanel);
                } else if (model != null) {
                    Date now = new Date();
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(now);
                    cal.add(Calendar.DAY_OF_YEAR, -1);
                    Date oneDayBefore = cal.getTime();
                    for (final GroupedExceptionSearch group : model.searchGroups) {
                        if (!isShowZeroSolutionSearches() && group.numberOfSolutions == 0) {
                            // filtered because there is no solution for it
                        } else if (!isShowRecurringSearches() && group.firstSeenSimilar.before(oneDayBefore)) {
                            // filtered because it is old
                        } else {
                            SearchGroupCardView searchGroupCard = new SearchGroupCardView(group);
                            searchGroups.add(searchGroupCard);
                            searchGroupCard.titleLabel.addMouseListener(new MouseAdapter() {
                                @Override
                                public void mouseClicked(MouseEvent e) {
                                    super.mouseClicked(e);
                                    ServiceManager.getService(project, SearchTabControllers.class).openSearchTab(group.lastSearch.searchId);
                                }
                            });
                            view.contentPanel.add(searchGroupCard.controlPanel);
                        }
                    }
                    if (searchGroups.isEmpty()) {
                        EmptyWarningPanel panel = new EmptyWarningPanel();
                        if (model.searchGroups.isEmpty()) {
                            panel.label.setText(SamebugBundle.message("samebug.toolwindow.history.content.noSearches"));
                        } else {
                            panel.label.setText(SamebugBundle.message("samebug.toolwindow.history.content.noVisibleSearches", SamebugIcons.calendarUrl, SamebugIcons.lightbulbUrl));
                        }
                        view.contentPanel.add(panel.controlPanel);
                    }
                }
                view.controlPanel.revalidate();
                view.controlPanel.repaint();
            }
        });
    }

    public boolean isShowZeroSolutionSearches() {
        return showZeroSolutionSearches;
    }

    public void setShowZeroSolutionSearches(boolean showZeroSolutionSearches) {
        this.showZeroSolutionSearches = showZeroSolutionSearches;
    }

    public boolean isShowRecurringSearches() {
        return showRecurringSearches;
    }

    public void setShowRecurringSearches(boolean showRecurringSearches) {
        this.showRecurringSearches = showRecurringSearches;
    }

    class ConnectionStatusUpdater implements ConnectionStatusListener {
        @Override
        public void startRequest() {
            ApplicationManager.getApplication().invokeLater(new Runnable() {
                @Override
                public void run() {
                    view.statusIcon.setIcon(SamebugIcons.linkActive);
                    view.statusIcon.setToolTipText(SamebugBundle.message("samebug.toolwindow.history.connectionStatus.description.loading"));
                    view.statusIcon.repaint();
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
                        view.statusIcon.repaint();
                    }
                }
            });
        }
    }

    class HistoryUpdater implements HistoryListener {

        @Override
        public void startLoading() {
            emptyHistoryPane();
        }

        @Override
        public void update(GroupedHistory history) {
            model = history;
            refreshHistoryPane();
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
