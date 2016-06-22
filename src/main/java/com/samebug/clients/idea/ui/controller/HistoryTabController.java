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
import com.intellij.openapi.ui.popup.Balloon;
import com.intellij.ui.awt.RelativePoint;
import com.intellij.util.messages.MessageBusConnection;
import com.samebug.clients.common.services.HistoryService;
import com.samebug.clients.idea.components.application.ClientService;
import com.samebug.clients.idea.components.application.IdeaSamebugPlugin;
import com.samebug.clients.idea.components.project.TutorialProjectComponent;
import com.samebug.clients.idea.messages.model.ConnectionStatusListener;
import com.samebug.clients.idea.messages.client.HistoryModelListener;
import com.samebug.clients.idea.messages.view.HistoryViewListener;
import com.samebug.clients.idea.resources.SamebugBundle;
import com.samebug.clients.idea.ui.component.TutorialPanel;
import com.samebug.clients.idea.ui.component.tab.HistoryTabView;
import com.samebug.clients.idea.ui.listeners.ConnectionStatusUpdater;
import com.samebug.clients.search.api.entities.SearchGroup;
import com.samebug.clients.search.api.entities.SearchHistory;
import com.samebug.clients.search.api.exceptions.SamebugClientException;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.List;

final public class HistoryTabController implements HistoryViewListener, HistoryModelListener {
    final static Logger LOGGER = Logger.getInstance(HistoryTabController.class);
    @NotNull
    final Project project;
    @NotNull
    final HistoryTabView view;
    @NotNull
    final ConnectionStatusUpdater statusUpdater;

    @NotNull
    final HistoryService service;

    public HistoryTabController(@NotNull Project project) {
        this.project = project;
        view = new HistoryTabView();
        service = ServiceManager.getService(project, HistoryService.class);
        statusUpdater = new ConnectionStatusUpdater(view.statusIcon);

        MessageBusConnection appMessageBus = ApplicationManager.getApplication().getMessageBus().connect(project);
        appMessageBus.subscribe(ConnectionStatusListener.TOPIC, statusUpdater);
        MessageBusConnection projectMessageBus = project.getMessageBus().connect(project);
        projectMessageBus.subscribe(HistoryViewListener.TOPIC, this);
        projectMessageBus.subscribe(HistoryModelListener.TOPIC, this);
    }

    @NotNull
    public JPanel getControlPanel() {
        return view;
    }


    public void setZeroSolutionFilter(boolean showZeroSolutionSearches) {
        service.setShowZeroSolutionSearches(showZeroSolutionSearches);
        refreshHistoryPane();
    }

    public void setRecurringFilter(boolean showRecurringSearches) {
        service.setShowRecurringSearches(showRecurringSearches);
        refreshHistoryPane();
        TutorialProjectComponent.withTutorialProject(project, new HideRecurringSearchesTutorial(showRecurringSearches));
    }

    public void reload() {
        ApplicationManager.getApplication().executeOnPooledThread(new Runnable() {
            @Override
            public void run() {
                ClientService client = IdeaSamebugPlugin.getInstance().getClient();
                try {
                    client.getSearchHistory();
                } catch (SamebugClientException e1) {
                    // TODO log?
                }
            }
        });
    }

    void refreshHistoryPane() {
        ApplicationManager.getApplication().assertIsDispatchThread();
        ClientService connectionService = IdeaSamebugPlugin.getInstance().getClient();
        final List<SearchGroup> groups = service.getVisibleHistory();
        if (!connectionService.isConnected()) {
            view.setErrorNotConnected();
        } else if (connectionService.isConnected() && !connectionService.isAuthenticated()) {
            view.setErrorNotLoggedIn();
        } else if (groups != null) {
            int visibleSearches = groups.size();
            int allSearches = service.unfilteredHistoryLength();
            if (visibleSearches == 0) {
                if (allSearches == 0) {
                    view.setErrorNoSearches();
                } else {
                    view.setErrorNoVisibleSearches();
                }
            } else {
                view.setHistory(groups);
            }
        } else {
            view.setErrorOther();
        }
        view.revalidate();
        view.repaint();
        TutorialProjectComponent.withTutorialProject(project, new HistoryTabTutorial());
    }

    @Override
    public void start() {
        // TODO update the view to show process
        LOGGER.info("start");
    }

    @Override
    public void success(final SearchHistory result) {
        service.setHistory(result);
        ApplicationManager.getApplication().invokeLater(new Runnable() {
            @Override
            public void run() {
                refreshHistoryPane();
            }
        });
    }

    @Override
    public void fail(Exception e) {
        service.setHistory(null);
        ApplicationManager.getApplication().invokeLater(new Runnable() {
            @Override
            public void run() {
                refreshHistoryPane();
            }
        });
    }

    @Override
    public void finish() {
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
