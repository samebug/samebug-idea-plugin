/**
 * Copyright 2017 Samebug, Inc.
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
package com.samebug.clients.idea.ui.controller.search;

import com.intellij.ide.DataManager;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.actionSystem.DataProvider;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.util.messages.MessageBusConnection;
import com.samebug.clients.common.search.api.entities.*;
import com.samebug.clients.common.services.ClientService;
import com.samebug.clients.common.services.DeprecatedSearchService;
import com.samebug.clients.idea.components.application.IdeaSamebugPlugin;
import com.samebug.clients.idea.components.application.Tracking;
import com.samebug.clients.idea.components.project.ToolWindowController;
import com.samebug.clients.idea.messages.view.WriteTipListener;
import com.samebug.clients.idea.resources.SamebugBundle;
import com.samebug.clients.idea.tracking.Events;
import com.samebug.clients.idea.ui.BrowserUtil;
import com.samebug.clients.idea.ui.component.tab.SearchTabView;
import com.samebug.clients.idea.ui.controller.TabController;
import com.samebug.clients.idea.ui.listeners.ConnectionStatusUpdater;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.net.URL;

final public class SearchTabController implements TabController, Disposable {
    final static Logger LOGGER = Logger.getInstance(SearchTabController.class);
    @NotNull
    final ToolWindowController twc;
    @NotNull
    final Project project;
    @NotNull
    final SearchTabView view;
    @NotNull
    final ConnectionStatusUpdater connectionStatusUpdater;
    final int mySearchId;
    @NotNull
    final DeprecatedSearchService service;

    @NotNull
    final HitConverter hitConverter;
    @NotNull
    final ViewController viewController;
    @NotNull
    final ModelController modelController;
    @NotNull
    final MarkModelController markModelController;
    @NotNull
    final TipModelController tipModelController;
    @NotNull
    final UserProfileController userProfileController;
    @NotNull
    final TrackingController trackingController;

    public SearchTabController(@NotNull ToolWindowController twc, @NotNull Project project, final int searchId) {
        this.twc = twc;
        this.project = project;
        view = new SearchTabView();
        connectionStatusUpdater = new ConnectionStatusUpdater(view.statusIcon);
        this.mySearchId = searchId;
        service = new DeprecatedSearchService(searchId);
        hitConverter = new HitConverter(this);
        viewController = new ViewController(this);
        modelController = new ModelController(this);
        markModelController = new MarkModelController(this);
        tipModelController = new TipModelController(this);
        userProfileController = new UserProfileController(this);
        trackingController = new TrackingController(this);

        DataManager.registerDataProvider(view, new MyDataProvider());
        MessageBusConnection projectConnection = project.getMessageBus().connect(this);
        projectConnection.subscribe(ConnectionStatusUpdater.TOPIC, connectionStatusUpdater);

    }

    @NotNull
    public JPanel getControlPanel() {
        return view;
    }

    public void reload() {
        ApplicationManager.getApplication().executeOnPooledThread(new Runnable() {
            @Override
            public void run() {
                ClientService client = IdeaSamebugPlugin.getInstance().getClient();
                // TODO
//                try {
//                    final Solutions solutions = client.loadSolutions(mySearchId);
//                    ApplicationManager.getApplication().executeOnPooledThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            java.util.List<URL> imageUrls = new ArrayList<URL>();
//                            try {
//                                imageUrls.add(new URL(IdeaSamebugPlugin.getInstance().getState().avatarUrl));
//                            } catch (Throwable e) {
//                                LOGGER.warn("Failed to load user's avatar", e);
//                            }
//                            for (final RestHit<Tip> tip : solutions.getTips()) {
//                                imageUrls.add(tip.getSolution().getAuthor().getAvatarUrl());
//                            }
//                            for (final RestHit<SolutionReference> s : solutions.getReferences()) {
//                                imageUrls.add(IdeaSamebugPlugin.getInstance().getUrlBuilder().sourceIcon(s.getSolution().getSource().getIcon()));
//                            }
//
//                            ImageUtil.loadImages(imageUrls);
//                            ApplicationManager.getApplication().invokeLater(new Runnable() {
//                                public void run() {
//                                    refreshTab();
//                                }
//                            });
//                        }
//                    });
//
//                } catch (SamebugClientException e1) {
//                    LOGGER.warn("Failed to download solutions for search " + mySearchId, e1);
//                }
            }
        });
    }


    void refreshTab() {
        ApplicationManager.getApplication().assertIsDispatchThread();
        final Solutions solutions = service.getSolutions();
        final ClientService connectionService = IdeaSamebugPlugin.getInstance().getClient();

        if (!connectionService.isConnected()) {
            view.setWarningNotConnected();
        } else if (connectionService.isConnected() && !connectionService.isAuthenticated()) {
            view.setWarningNotLoggedIn();
        } else if (solutions != null) {
            view.setSolutions(hitConverter.convertSolutions(solutions), new Actions());
        } else {
            view.setWarningOther();
        }

        view.revalidate();
        view.repaint();
    }

    @Override
    public void dispose() {

    }

    private class MyDataProvider implements DataProvider {
        @Override
        public Object getData(@NonNls final String dataId) {
            if (ToolWindowController.DATA_KEY.is(dataId)) return SearchTabController.this;
            else return null;
        }
    }

    private final class Actions implements SearchTabView.Actions {

        @Override
        public void onClickWriteTip() {
            project.getMessageBus().syncPublisher(WriteTipListener.TOPIC).openWriteTip(SearchTabController.this);
        }

        @Override
        public void onClickCancel() {
            project.getMessageBus().syncPublisher(WriteTipListener.TOPIC).cancelWriteTip(SearchTabController.this);
        }

        @Override
        public void onClickSubmitTip(String tip, String rawSourceUrl) {
            project.getMessageBus().syncPublisher(WriteTipListener.TOPIC).submitTip(SearchTabController.this, tip, rawSourceUrl);
        }

        @Override
        public String getTitleMouseOverText(SearchGroup group) {
            return SamebugBundle.message("samebug.toolwindow.search.search.title.tooltip", group.getLastSearch().getId());
        }

        @Override
        public void onClickTitle(SearchGroup group) {
            URL link = IdeaSamebugPlugin.getInstance().getUrlBuilder().search(group.getLastSearch().getId());
            BrowserUtil.browse(link);
            Tracking.projectTracking(project).trace(Events.linkClick(project, link));
        }
    }
}

