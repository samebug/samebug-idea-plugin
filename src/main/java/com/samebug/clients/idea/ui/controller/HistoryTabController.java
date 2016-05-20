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
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.popup.Balloon;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.ui.awt.RelativePoint;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentManager;
import com.samebug.clients.idea.components.application.ApplicationSettings;
import com.samebug.clients.idea.components.application.IdeaClientService;
import com.samebug.clients.idea.components.application.IdeaSamebugPlugin;
import com.samebug.clients.idea.components.project.TutorialProjectComponent;
import com.samebug.clients.idea.resources.SamebugBundle;
import com.samebug.clients.idea.resources.SamebugIcons;
import com.samebug.clients.idea.ui.component.TutorialPanel;
import com.samebug.clients.idea.ui.component.card.SearchGroupCardView;
import com.samebug.clients.idea.ui.component.tab.HistoryTabView;
import com.samebug.clients.idea.ui.layout.EmptyWarningPanel;
import com.samebug.clients.idea.ui.listeners.ConnectionStatusUpdater;
import com.samebug.clients.idea.ui.listeners.SearchTabOpener;
import com.samebug.clients.search.api.entities.SearchGroup;
import com.samebug.clients.search.api.entities.StackTraceSearchGroup;
import com.samebug.clients.search.api.entities.SearchHistory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.Calendar;
import java.util.Date;

final public class HistoryTabController {
    final static Logger LOGGER = Logger.getInstance(HistoryTabController.class);
    @NotNull
    final Project project;
    @NotNull
    final HistoryTabView view;
    @NotNull
    final ConnectionStatusUpdater statusUpdater;

    @Nullable
    SearchHistory model;
    boolean showZeroSolutionSearches;
    boolean showRecurringSearches;


    public HistoryTabController(@NotNull Project project) {
        ApplicationSettings settings = IdeaSamebugPlugin.getInstance().getState();
        this.project = project;
        view = new HistoryTabView();
        showZeroSolutionSearches = settings.showZeroSolutions;
        showRecurringSearches = settings.showRecurring;
        statusUpdater = new ConnectionStatusUpdater(view.statusIcon);
    }

    @NotNull
    public ConnectionStatusUpdater getStatusUpdater() {
        return statusUpdater;
    }

    @NotNull
    public JPanel getControlPanel() {
        return view.controlPanel;
    }

    public boolean isShowZeroSolutionSearches() {
        return showZeroSolutionSearches;
    }

    public boolean isShowRecurringSearches() {
        return showRecurringSearches;
    }

    // TODO application settings should not be changed via HistoryTabController, but vica versa.
    public void setShowZeroSolutionSearches(boolean showZeroSolutionSearches) {
        ApplicationManager.getApplication().assertIsDispatchThread();
        ApplicationSettings settings = IdeaSamebugPlugin.getInstance().getState();
        settings.showZeroSolutions = showZeroSolutionSearches;
        this.showZeroSolutionSearches = showZeroSolutionSearches;
        refreshHistoryPane();
    }

    public void setShowRecurringSearches(boolean showRecurringSearches) {
        ApplicationManager.getApplication().assertIsDispatchThread();
        ApplicationSettings settings = IdeaSamebugPlugin.getInstance().getState();
        settings.showRecurring = showRecurringSearches;
        this.showRecurringSearches = showRecurringSearches;
        refreshHistoryPane();
        TutorialProjectComponent.withTutorialProject(project, new HideRecurringSearchesTutorial(showRecurringSearches));
    }

    // TODO this method probably not belongs here, but to a higher level samebug tool window controller
    public void focus() {
        ApplicationManager.getApplication().assertIsDispatchThread();
        final ToolWindow toolWindow = ToolWindowManager.getInstance(project).getToolWindow("Samebug");
        final ContentManager toolwindowCM = toolWindow.getContentManager();
        final Content content = toolwindowCM.getContent(getControlPanel());
        if (content != null) toolwindowCM.setSelectedContent(content);
        toolWindow.show(null);
    }

    public void update(SearchHistory history) {
        ApplicationManager.getApplication().assertIsDispatchThread();
        model = history;
        refreshHistoryPane();
    }

    // TODO move connection status error panel elsewhere
    void refreshHistoryPane() {
        IdeaClientService connectionService = IdeaSamebugPlugin.getInstance().getClient();
        view.contentPanel.removeAll();
        if (!connectionService.isConnected()) {
            EmptyWarningPanel panel = new EmptyWarningPanel();
            panel.label.setText(SamebugBundle.message("samebug.toolwindow.history.content.notConnected", IdeaSamebugPlugin.getInstance().getUrlBuilder().getServerRoot()));
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
            int visibleSearches = 0;
            for (final SearchGroup g : model.searchGroups) {
                // TODO handle text search groups
                StackTraceSearchGroup group = (StackTraceSearchGroup) g;
                if (!isShowZeroSolutionSearches() && group.numberOfHits == 0) {
                    // filtered because there is no solution for it
                } else if (!isShowRecurringSearches() && group.firstSeen.before(oneDayBefore)) {
                    // filtered because it is old
                } else {
                    visibleSearches++;
                    SearchGroupCardView searchGroupCard = new SearchGroupCardView(group);
                    searchGroupCard.titleLabel.addMouseListener(new SearchTabOpener(project, group.lastSearch.id));
                    view.contentPanel.add(searchGroupCard);
                }
            }
            if (visibleSearches == 0) {
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
        TutorialProjectComponent.withTutorialProject(project, new HistoryTabTutorial());
    }

    class HistoryTabTutorial extends TutorialProjectComponent.TutorialProjectAnonfun<Void> {
        @Override
        public Void call() {
            if (settings.historyTab) {
                settings.historyTab = false;
                final JPanel tutorialPanel = new TutorialPanel(SamebugBundle.message("samebug.tutorial.historyTab.title"),
                        SamebugBundle.message("samebug.tutorial.historyTab.message"));
                Balloon balloon = TutorialProjectComponent.createTutorialBalloon(project, tutorialPanel);
                balloon.show(RelativePoint.getNorthWestOf(view.toolbarPanel), Balloon.Position.atLeft);
            }
            return null;
        }
    }

    class HideRecurringSearchesTutorial extends TutorialProjectComponent.TutorialProjectAnonfun<Void> {
        final boolean showRecurringSearches;

        public HideRecurringSearchesTutorial(boolean showRecurringSearches) {
            this.showRecurringSearches = showRecurringSearches;
        }

        @Override
        public Void call() {
            if (!showRecurringSearches && settings.recurringExceptionsFilter) {
                settings.recurringExceptionsFilter = false;
                final JPanel tutorialPanel = new TutorialPanel(SamebugBundle.message("samebug.tutorial.recurringExceptionsFilter.title"),
                        SamebugBundle.message("samebug.tutorial.recurringExceptionsFilter.message"));
                Balloon balloon = TutorialProjectComponent.createTutorialBalloon(project, tutorialPanel);
                balloon.show(RelativePoint.getNorthWestOf(view.toolbarPanel), Balloon.Position.above);
            }
            return null;
        }
    }
}
