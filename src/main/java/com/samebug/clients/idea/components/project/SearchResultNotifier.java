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
import com.intellij.openapi.Disposable;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.popup.Balloon;
import com.intellij.openapi.wm.IdeFrame;
import com.intellij.ui.awt.RelativePoint;
import com.intellij.util.containers.HashMap;
import com.intellij.util.messages.MessageBusConnection;
import com.samebug.clients.common.entities.ExceptionType;
import com.samebug.clients.common.services.HistoryService;
import com.samebug.clients.common.ui.Colors;
import com.samebug.clients.idea.components.application.ClientService;
import com.samebug.clients.idea.components.application.IdeaSamebugPlugin;
import com.samebug.clients.idea.components.application.TutorialApplicationComponent;
import com.samebug.clients.idea.components.application.TutorialSettings;
import com.samebug.clients.idea.messages.model.BatchStackTraceSearchListener;
import com.samebug.clients.idea.notification.SamebugNotifications;
import com.samebug.clients.idea.notification.SearchResultsNotification;
import com.samebug.clients.idea.resources.SamebugBundle;
import com.samebug.clients.idea.resources.SamebugIcons;
import com.samebug.clients.search.api.entities.SearchGroup;
import com.samebug.clients.search.api.entities.SearchHistory;
import com.samebug.clients.search.api.entities.SearchResults;
import com.samebug.clients.search.api.entities.StackTraceSearchGroup;
import com.samebug.clients.search.api.exceptions.SamebugClientException;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

final class SearchResultNotifier implements BatchStackTraceSearchListener, Disposable {
    final static int NOTIFICATION_EXPIRATION_DELAY = 10000;
    final static Logger LOGGER = Logger.getInstance(SearchResultNotifier.class);

    @NotNull
    final Project project;
    @NotNull
    final HistoryService service;

    @NotNull
    private MessageBusConnection messageBusConnection;

    public SearchResultNotifier(@NotNull Project project) {
        this.project = project;
        service = ServiceManager.getService(project, HistoryService.class);
        messageBusConnection = project.getMessageBus().connect();
        messageBusConnection.subscribe(BatchStackTraceSearchListener.TOPIC, this);
    }

    @Override
    public void batchStart() {

    }

    @Override
    public void batchFinished(final List<SearchResults> results, int failed) {
        Long timelimitForFreshSearch = new Date().getTime() - (1 * 60 * 1000);
        Map<String, StackTraceSearchGroup> groupsByStackTraceId = new HashMap<String, StackTraceSearchGroup>();
        ClientService client = IdeaSamebugPlugin.getInstance().getClient();
        try {
            SearchHistory history = client.getSearchHistory();
            for (SearchGroup s : history.searchGroups) {
                if (s instanceof StackTraceSearchGroup) {
                    StackTraceSearchGroup sg = (StackTraceSearchGroup) s;
                    groupsByStackTraceId.put(sg.lastSearch.stackTrace.stackTraceId, sg);
                }
            }

            boolean isShowRecurringSearches = ServiceManager.getService(project, HistoryService.class).isShowRecurringSearches();
            boolean isShowZeroSolutionSearches = ServiceManager.getService(project, HistoryService.class).isShowZeroSolutionSearches();
            final Map<String, SearchResults> searchesByStackTraceId = new HashMap<String, SearchResults>();
            final Map<Integer, StackTraceSearchGroup> resultsBySearchId = new HashMap<Integer, StackTraceSearchGroup>();
            for (SearchResults result : results) {
                searchesByStackTraceId.put(result.stackTraceId, result);
            }

            int recurrings = 0;
            int zeroSolutions = 0;
            final List<Integer> searchIds = new ArrayList<Integer>();
            for (SearchResults result : searchesByStackTraceId.values()) {
                String stackTraceId = result.stackTraceId;
                StackTraceSearchGroup historyResult = groupsByStackTraceId.get(stackTraceId);
                if (historyResult != null && historyResult.numberOfHits == 0) {
                    if (isShowZeroSolutionSearches) {
                        ++zeroSolutions;
                        searchIds.add(result.searchId);
                        resultsBySearchId.put(result.searchId, groupsByStackTraceId.get(result.stackTraceId));
                    }
                } else if (historyResult != null && historyResult.numberOfSearches > 1
                        && historyResult.firstSeen.getTime() < timelimitForFreshSearch) {
                    if (isShowRecurringSearches) {
                        ++recurrings;
                        searchIds.add(result.searchId);
                        resultsBySearchId.put(result.searchId, groupsByStackTraceId.get(result.stackTraceId));
                    }
                }
            }
            final int nRecurringSearches = recurrings;
            final int nSearchesWithZeroSolutions = zeroSolutions;

            ApplicationManager.getApplication().invokeLater(new Runnable() {
                @Override
                public void run() {
                    if (searchIds.size() == 0) {
                        // all searches filtered out, show no notification
                    } else {
                        // there are searches to report about
                        final TutorialSettings settings = ApplicationManager.getApplication().getComponent(TutorialApplicationComponent.class).getState();
                        TutorialProjectComponent tutorialComponent = project.getComponent(TutorialProjectComponent.class);
                        assert settings != null;
                        assert tutorialComponent != null;

                        if (nSearchesWithZeroSolutions == 0 && nRecurringSearches == 0) {
                            if (searchIds.size() == 1) {
                                // 1 new exception with solutions
                                StackTraceSearchGroup search = resultsBySearchId.get(searchIds.get(0));
                                String exceptionSummary = summarizeException(search.getLastSearch().stackTrace.trace);
                                showNotification(SamebugBundle.message("samebug.notification.searchresults.one", searchIds.get(0).toString(), exceptionSummary));
                            } else {
                                // 2+ new exceptions with solutions
                                showNotification(SamebugBundle.message("samebug.notification.searchresults.multiple", searchIds.size()));
                            }
                        } else if (nSearchesWithZeroSolutions == 0 && nRecurringSearches > 0) {
                            if (searchIds.size() == 1) {
                                // 1 recurring exception with solutions
                                StackTraceSearchGroup search = resultsBySearchId.get(searchIds.get(0));
                                String exceptionSummary = summarizeException(search.getLastSearch().stackTrace.trace);
                                if (settings.searchResultsRecurring) {
                                    settings.searchResultsRecurring = false;
                                    settings.searchResultsMixed = false;
                                    showTutorialNotification(SamebugBundle.message("samebug.tutorial.searchResults.oneRecurring", searchIds.get(0).toString(), SamebugIcons.calendarUrl, exceptionSummary));
                                } else {
                                    showNotification(SamebugBundle.message("samebug.notification.searchresults.oneRecurring", searchIds.get(0).toString(), exceptionSummary));
                                }
                            } else {
                                // 2+ recurring exception with solutions
                                if (settings.searchResultsRecurring) {
                                    settings.searchResultsRecurring = false;
                                    settings.searchResultsMixed = false;
                                    showTutorialNotification(SamebugBundle.message("samebug.tutorial.searchResults.multipleRecurring", searchIds.size(), SamebugIcons.calendarUrl));
                                } else {
                                    showNotification(SamebugBundle.message("samebug.notification.searchresults.multipleRecurring", searchIds.size()));
                                }
                            }
                        } else if (nSearchesWithZeroSolutions > 0 && nRecurringSearches == 0) {
                            // 1+ new exception without solution
                            if (settings.searchResultsZeroSolutions) {
                                settings.searchResultsZeroSolutions = false;
                                settings.searchResultsMixed = false;
                                showTutorialNotification(SamebugBundle.message("samebug.tutorial.searchResults.zeroSolutions", searchIds.size(), SamebugIcons.lightbulbUrl));
                            } else {
                                showNotification(SamebugBundle.message("samebug.notification.searchresults.zeroSolutions", searchIds.size()));
                            }
                        } else {
                            // other cases.
                            if (settings.searchResultsMixed) {
                                settings.searchResultsMixed = false;
                                showTutorialNotification(
                                        SamebugBundle.message("samebug.tutorial.searchResults.mixed", searchIds.size(), SamebugIcons.calendarUrl, SamebugIcons.lightbulbUrl));
                            } else {
                                showNotification(SamebugBundle.message("samebug.notification.searchresults.mixed", searchIds.size()));
                            }
                        }
                    }
                }
            });
        } catch (SamebugClientException e1) {
            LOGGER.warn("Failed to load history after searching and building result notification.");
        }
    }

    private String summarizeException(com.samebug.clients.search.api.entities.Exception exception) {
        ExceptionType exceptionType = new ExceptionType(exception.typeName);
        String shortMessage = exception.message == null ? null : exception.message.length() > 25 ? exception.message.substring(0, 22) + "..." : exception.message;
        if (shortMessage == null) return SamebugBundle.message("samebug.notification.exceptionSummary.withoutMessage", exceptionType.className);
        else return SamebugBundle.message("samebug.notification.exceptionSummary.withMessage", exceptionType.className, shortMessage);
    }

    private void showNotification(String message) {
        final SearchResultsNotification notification = new SearchResultsNotification(project, message);

        final Timer timer = new Timer(NOTIFICATION_EXPIRATION_DELAY, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                notification.expire();
            }
        });

        ApplicationManager.getApplication().invokeLater(new Runnable() {
            public void run() {
                notification.notify(project);
                timer.start();
            }
        });
    }

    private void showTutorialNotification(final String message) {
        TutorialProjectComponent.createTutorialBalloon(project, new JEditorPane() {
            {
                setEditable(false);
                setOpaque(false);
                setBorder(BorderFactory.createEmptyBorder());
                setContentType("text/html");
                setText(message);
                setForeground(Colors.samebugWhite);
                addHyperlinkListener(SamebugNotifications.basicHyperlinkListener(project, "tutorial"));
            }
        }).show(RelativePoint.getNorthEastOf(((IdeFrame) NotificationsManagerImpl.findWindowForBalloon(project)).getComponent()), Balloon.Position.atLeft);
    }


    @Override
    public void dispose() {
        messageBusConnection.disconnect();
    }
}
