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
package com.samebug.clients.idea.ui.controller.history;

import com.intellij.ide.DataManager;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.actionSystem.DataProvider;
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
import com.samebug.clients.idea.components.project.ToolWindowController;
import com.samebug.clients.idea.components.project.TutorialProjectComponent;
import com.samebug.clients.idea.messages.model.ConnectionStatusListener;
import com.samebug.clients.idea.messages.view.HistoryViewListener;
import com.samebug.clients.idea.messages.view.SearchGroupCardListener;
import com.samebug.clients.idea.resources.SamebugBundle;
import com.samebug.clients.idea.ui.component.TutorialPanel;
import com.samebug.clients.idea.ui.component.tab.HistoryTabView;
import com.samebug.clients.idea.ui.controller.TabController;
import com.samebug.clients.idea.ui.listeners.ConnectionStatusUpdater;
import com.samebug.clients.search.api.entities.SearchGroup;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.List;

final public class HistoryTabController implements TabController, Disposable, SearchGroupCardListener {
    final static Logger LOGGER = Logger.getInstance(HistoryTabController.class);
    @NotNull
    final ToolWindowController twc;
    @NotNull
    final Project myProject;
    @NotNull
    final HistoryTabView view;
    @NotNull
    final ConnectionStatusUpdater statusUpdater;
    @NotNull
    final ModelController modelController;
    @NotNull
    final TrackingController trackingController;

    @NotNull
    final HistoryService service;

    public HistoryTabController(@NotNull ToolWindowController twc, @NotNull Project project) {
        this.twc = twc;
        this.myProject = project;
        view = new HistoryTabView();
        service = ServiceManager.getService(project, HistoryService.class);
        statusUpdater = new ConnectionStatusUpdater(view.statusIcon);
        modelController = new ModelController(this);
        trackingController = new TrackingController(this);

        DataManager.registerDataProvider(view, new MyDataProvider());
        MessageBusConnection appMessageBus = ApplicationManager.getApplication().getMessageBus().connect(project);
        appMessageBus.subscribe(ConnectionStatusListener.TOPIC, statusUpdater);
        MessageBusConnection projectMessageBus = project.getMessageBus().connect(project);
        projectMessageBus.subscribe(SearchGroupCardListener.TOPIC, this);
    }

    @NotNull
    public JPanel getControlPanel() {
        return view;
    }

    void refreshTab() {
        ApplicationManager.getApplication().assertIsDispatchThread();
        final ClientService connectionService = IdeaSamebugPlugin.getInstance().getClient();
        final List<SearchGroup> groups = service.getVisibleHistory();
        if (!connectionService.isConnected()) {
            view.setWarningNotConnected();
        } else if (connectionService.isConnected() && !connectionService.isAuthenticated()) {
            view.setWarningNotLoggedIn();
        } else if (groups != null) {
            int visibleSearches = groups.size();
            int allSearches = service.unfilteredHistoryLength();
            if (visibleSearches == 0) {
                if (allSearches == 0) {
                    view.setWarningNoSearches();
                } else {
                    view.setWarningNoVisibleSearches();
                }
            } else {
                view.setHistory(groups);
            }
        } else {
            view.setWarningOther();
        }
        view.revalidate();
        view.repaint();
        TutorialProjectComponent.withTutorialProject(myProject, new HistoryTabTutorial());
    }

    @Override
    public void titleClick(@NotNull TabController tab, SearchGroup searchGroup) {
        if (this == tab) {
            twc.focusOnSearch(searchGroup.getLastSearch().id);
        }
    }

    @Override
    public void dispose() {
    }

    private class MyDataProvider implements DataProvider {
        @Override
        public Object getData(@NonNls final String dataId) {
            if (ToolWindowController.DATA_KEY.is(dataId)) return HistoryTabController.this;
            else return null;
        }
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

}
