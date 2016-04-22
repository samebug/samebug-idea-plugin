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
package com.samebug.clients.idea.components.project;

import com.intellij.notification.impl.NotificationsManagerImpl;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.AbstractProjectComponent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.popup.Balloon;
import com.intellij.openapi.wm.IdeFrame;
import com.intellij.ui.awt.RelativePoint;
import com.intellij.util.containers.HashMap;
import com.intellij.util.messages.MessageBusConnection;
import com.samebug.clients.idea.components.application.IdeaSamebugPlugin;
import com.samebug.clients.idea.components.application.TutorialApplicationComponent;
import com.samebug.clients.idea.components.application.TutorialSettings;
import com.samebug.clients.idea.messages.BatchStackTraceSearchListener;
import com.samebug.clients.idea.messages.HistoryListener;
import com.samebug.clients.idea.notification.SamebugNotifications;
import com.samebug.clients.idea.notification.SearchResultsNotification;
import com.samebug.clients.idea.resources.SamebugBundle;
import com.samebug.clients.idea.resources.SamebugIcons;
import com.samebug.clients.idea.ui.Colors;
import com.samebug.clients.idea.ui.controller.HistoryTabController;
import com.samebug.clients.search.api.entities.GroupedExceptionSearch;
import com.samebug.clients.search.api.entities.GroupedHistory;
import com.samebug.clients.search.api.entities.SearchResults;
import com.samebug.clients.search.api.exceptions.SamebugClientException;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

class SearchResultNotifier extends AbstractProjectComponent implements BatchStackTraceSearchListener {
    public SearchResultNotifier(Project project) {
        super(project);
    }

    @Override
    public void projectOpened() {
        super.projectOpened();
        messageBusConnection = myProject.getMessageBus().connect();
        messageBusConnection.subscribe(BatchStackTraceSearchListener.BATCH_SEARCH_TOPIC, this);
    }

    @Override
    public void projectClosed() {
        messageBusConnection.disconnect();
    }

    @Override
    public void batchStart() {

    }

    @Override
    public void batchFinished(final List<SearchResults> results, int failed) {
        Long timelimitForFreshSearch = new Date().getTime() - (1 * 60 * 1000);
        // TODO history does not contains the stack ids (or at least the deepest stack _id)
        Map<Integer, GroupedExceptionSearch> history = new HashMap<Integer, GroupedExceptionSearch>();
        try {
            final IdeaSamebugPlugin plugin = IdeaSamebugPlugin.getInstance();
            GroupedHistory h = plugin.getClient().getSearchHistory();
            if (!myProject.isDisposed()) myProject.getMessageBus().syncPublisher(HistoryListener.UPDATE_HISTORY_TOPIC).update(h);
            for (GroupedExceptionSearch s : h.searchGroups) {
                history.put(s.lastSearch.searchId, s);
            }
        } catch (SamebugClientException e1) {
            if (!myProject.isDisposed()) myProject.getMessageBus().syncPublisher(HistoryListener.UPDATE_HISTORY_TOPIC).update(null);
        }

        boolean isShowRecurringSearches = ServiceManager.getService(myProject, HistoryTabController.class).isShowRecurringSearches();
        boolean isShowZeroSolutionSearches = ServiceManager.getService(myProject, HistoryTabController.class).isShowZeroSolutionSearches();
        Map<Integer, SearchResults> groupedResults = new HashMap<Integer, SearchResults>();
        for (SearchResults result : results) {
            groupedResults.put(result.deepestStackId, result);
        }

        int recurrings = 0;
        int zeroSolutions = 0;
        final List<String> searchIds = new ArrayList<String>();
        for (SearchResults result : groupedResults.values()) {
            int searchId = Integer.parseInt(result.searchId);
            GroupedExceptionSearch historyResult = history.get(searchId);
            if (historyResult != null && historyResult.numberOfSolutions == 0) {
                if (isShowZeroSolutionSearches) {
                    ++zeroSolutions;
                    searchIds.add(result.searchId);
                }
            } else if (historyResult != null && historyResult.numberOfSimilars > 1
                    && historyResult.firstSeenSimilar != null && historyResult.firstSeenSimilar.getTime() < timelimitForFreshSearch) {
                if (isShowRecurringSearches) {
                    ++recurrings;
                    searchIds.add(result.searchId);
                }
            }
        }

        if (searchIds.size() == 0) {
            // all searches filtered out, show no notification
        } else {
            // there are searches to report about
            final TutorialSettings settings = ApplicationManager.getApplication().getComponent(TutorialApplicationComponent.class).getState();
            TutorialProjectComponent tutorialComponent = myProject.getComponent(TutorialProjectComponent.class);
            assert settings != null;
            assert tutorialComponent != null;

            if (zeroSolutions == 0 && recurrings == 0) {
                // new exceptions with solutions
                if (searchIds.size() == 1) {
                    showNotification(SamebugBundle.message("samebug.notification.searchresults.one", searchIds.get(0)));
                } else {
                    showNotification(SamebugBundle.message("samebug.notification.searchresults.multiple", searchIds.size()));
                }
            } else if (zeroSolutions == 0 && recurrings > 0) {
                if (searchIds.size() == 1) {
                    if (settings.searchResultsRecurring) {
                        settings.searchResultsRecurring = false;
                        settings.searchResultsMixed = false;
                        showTutorialNotification(SamebugBundle.message("samebug.tutorial.searchResults.oneRecurring", searchIds.get(0), SamebugIcons.calendarUrl));
                    } else {
                        showNotification(SamebugBundle.message("samebug.notification.searchresults.oneRecurring", searchIds.get(0)));
                    }
                } else {
                    if (settings.searchResultsRecurring) {
                        settings.searchResultsRecurring = false;
                        settings.searchResultsMixed = false;
                        showTutorialNotification(SamebugBundle.message("samebug.tutorial.searchResults.multipleRecurring", searchIds.size(), SamebugIcons.calendarUrl));
                    } else {
                        showNotification(SamebugBundle.message("samebug.notification.searchresults.multipleRecurring", searchIds.size()));
                    }
                }
            } else if (zeroSolutions > 0 && recurrings == 0) {
                if (settings.searchResultsZeroSolutions) {
                    settings.searchResultsZeroSolutions = false;
                    settings.searchResultsMixed = false;
                    showTutorialNotification(SamebugBundle.message("samebug.tutorial.searchResults.zeroSolutions", searchIds.size(), SamebugIcons.lightbulbUrl));
                } else {
                    showNotification(SamebugBundle.message("samebug.notification.searchresults.zeroSolutions", searchIds.size()));
                }
            } else {
                if (settings.searchResultsMixed) {
                    settings.searchResultsMixed = false;
                    showTutorialNotification(SamebugBundle.message("samebug.tutorial.searchResults.mixed", searchIds.size(), SamebugIcons.calendarUrl, SamebugIcons.lightbulbUrl));
                } else {
                    showNotification(SamebugBundle.message("samebug.notification.searchresults.mixed", searchIds.size()));
                }
            }
        }
    }

    private void showNotification(String message) {
        final SearchResultsNotification notification = new SearchResultsNotification(myProject, message);

        final Timer timer = new Timer(NOTIFICATION_EXPIRATION_DELAY, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                notification.expire();
            }
        });

        ApplicationManager.getApplication().invokeLater(new Runnable() {
            public void run() {
                notification.notify(myProject);
                timer.start();
            }
        });
    }

    private void showTutorialNotification(final String message) {
        TutorialProjectComponent.createTutorialBalloon(myProject, new JEditorPane() {
            {
                setEditable(false);
                setOpaque(false);
                setBorder(BorderFactory.createEmptyBorder());
                setContentType("text/html");
                setText(message);
                setForeground(Colors.samebugWhite);
                addHyperlinkListener(SamebugNotifications.basicHyperlinkListener(myProject, "tutorial"));
            }
        }).show(RelativePoint.getNorthEastOf(((IdeFrame) NotificationsManagerImpl.findWindowForBalloon(myProject)).getComponent()), Balloon.Position.atLeft);
    }

    private final static int NOTIFICATION_EXPIRATION_DELAY = 10000;
    private MessageBusConnection messageBusConnection;

}
