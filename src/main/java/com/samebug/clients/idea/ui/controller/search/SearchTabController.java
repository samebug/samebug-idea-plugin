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
package com.samebug.clients.idea.ui.controller.search;

import com.intellij.ide.DataManager;
import com.intellij.openapi.actionSystem.DataProvider;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.util.messages.MessageBusConnection;
import com.samebug.clients.common.services.SearchService;
import com.samebug.clients.idea.components.application.ClientService;
import com.samebug.clients.idea.components.application.IdeaSamebugPlugin;
import com.samebug.clients.idea.components.project.ToolWindowController;
import com.samebug.clients.idea.ui.ImageUtil;
import com.samebug.clients.idea.ui.component.card.ExternalSolutionView;
import com.samebug.clients.idea.ui.component.card.SamebugTipView;
import com.samebug.clients.idea.ui.component.organism.MarkPanel;
import com.samebug.clients.idea.ui.component.tab.SearchTabView;
import com.samebug.clients.idea.ui.controller.TabController;
import com.samebug.clients.idea.ui.listeners.ConnectionStatusUpdater;
import com.samebug.clients.search.api.entities.*;
import com.samebug.clients.search.api.exceptions.SamebugClientException;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

final public class SearchTabController implements TabController {
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
    final SearchService service;

    @NotNull
    final ViewController viewController;
    @NotNull
    final ModelController modelController;
    @NotNull
    final TipModelController tipModelController;

    public SearchTabController(@NotNull ToolWindowController twc, @NotNull Project project, final int searchId) {
        this.twc = twc;
        this.project = project;
        view = new SearchTabView();
        connectionStatusUpdater = new ConnectionStatusUpdater(view.statusIcon);
        this.mySearchId = searchId;
        service = new SearchService(searchId);
        viewController = new ViewController(this);
        modelController = new ModelController(this);
        tipModelController = new TipModelController(this);

        DataManager.registerDataProvider(view, new MyDataProvider());
        MessageBusConnection projectMessageBus = project.getMessageBus().connect(project);
        projectMessageBus.subscribe(ConnectionStatusUpdater.TOPIC, connectionStatusUpdater);

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
                try {
                    final Solutions solutions = client.getSolutions(mySearchId);
                    ApplicationManager.getApplication().executeOnPooledThread(new Runnable() {
                        @Override
                        public void run() {
                            java.util.List<URL> imageUrls = new ArrayList<URL>();
                            try {
                                imageUrls.add(new URL(IdeaSamebugPlugin.getInstance().getState().avatarUrl));
                            } catch (Throwable e) {
                                LOGGER.warn("Failed to load user's avatar", e);
                            }
                            for (final RestHit<Tip> tip : solutions.tips) {
                                imageUrls.add(tip.solution.author.avatarUrl);
                            }
                            for (final RestHit<SolutionReference> s : solutions.references) {
                                imageUrls.add(IdeaSamebugPlugin.getInstance().getUrlBuilder().sourceIcon(s.solution.source.icon));
                            }

                            ImageUtil.loadImages(imageUrls);
                            ApplicationManager.getApplication().invokeLater(new Runnable() {
                                public void run() {
                                    refreshTab();
                                }
                            });
                        }
                    });

                } catch (SamebugClientException e1) {
                    LOGGER.warn("Failed to download solutions for search " + mySearchId, e1);
                }
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
            view.setSolutions(convertSolutions(solutions));
        } else {
            view.setWarningOther();
        }

        view.revalidate();
        view.repaint();
    }

    SearchTabView.Model convertSolutions(@NotNull final Solutions solutions) {
        return new SearchTabView.Model() {

            @Override
            public SearchGroup getSearch() {
                return solutions.searchGroup;
            }

            @Override
            public List<ExternalSolutionView.Model> getReferences() {
                List<ExternalSolutionView.Model> result = new ArrayList<ExternalSolutionView.Model>(solutions.references.size());
                for (RestHit<SolutionReference> reference : solutions.references) {
                    result.add(convertReference(solutions.searchGroup, reference));
                }
                return result;
            }

            @Override
            public List<SamebugTipView.Model> getTips() {
                List<SamebugTipView.Model> result = new ArrayList<SamebugTipView.Model>(solutions.tips.size());
                for (RestHit<Tip> tip : solutions.tips) {
                    result.add(convertTip(solutions.searchGroup, tip));
                }
                return result;
            }
        };
    }

    // TODO reuse the two specifications
    MarkPanel.Model convertHit(final SearchGroup search, final RestHit hit) {
        return new MarkPanel.Model() {

            @NotNull
            @Override
            public RestHit getHit() {
                return hit;
            }

            @NotNull
            @Override
            public int getSearchId() {
                return search.getLastSearch().id;
            }

            @Override
            public boolean canBeMarked() {
                return SearchService.canBeMarked(IdeaSamebugPlugin.getInstance().getState().userId, search, hit);
            }

            @Override
            public boolean createdByCurrentUser() {
                return SearchService.createdByUser(IdeaSamebugPlugin.getInstance().getState().userId, hit);
            }
        };
    }

    SamebugTipView.Model convertTip(final SearchGroup search, final RestHit<Tip> hit) {
        return new SamebugTipView.Model() {
            @NotNull
            @Override
            public RestHit<Tip> getHit() {
                return hit;
            }

            @NotNull
            @Override
            public int getSearchId() {
                return search.getLastSearch().id;
            }

            @NotNull
            @Override
            public List<BreadCrumb> getMatchingBreadCrumb() {
                return SearchService.getMatchingBreadCrumb(search, hit);
            }

            @Override
            public boolean canBeMarked() {
                return SearchService.canBeMarked(IdeaSamebugPlugin.getInstance().getState().userId, search, hit);
            }

            @Override
            public boolean createdByCurrentUser() {
                return SearchService.createdByUser(IdeaSamebugPlugin.getInstance().getState().userId, hit);
            }
        };
    }

    ExternalSolutionView.Model convertReference(final SearchGroup search, final RestHit<SolutionReference> hit) {
        return new ExternalSolutionView.Model() {

            @NotNull
            @Override
            public RestHit<SolutionReference> getHit() {
                return hit;
            }

            @NotNull
            @Override
            public int getSearchId() {
                return search.getLastSearch().id;
            }

            @Override
            public boolean canBeMarked() {
                return SearchService.canBeMarked(IdeaSamebugPlugin.getInstance().getState().userId, search, hit);
            }

            @Override
            public boolean createdByCurrentUser() {
                return SearchService.createdByUser(IdeaSamebugPlugin.getInstance().getState().userId, hit);
            }

            @NotNull
            @Override
            public List<BreadCrumb> getMatchingBreadCrumb() {
                return SearchService.getMatchingBreadCrumb(search, hit);
            }
        };
    }

    private class MyDataProvider implements DataProvider {
        @Override
        public Object getData(@NonNls final String dataId) {
            if (ToolWindowController.DATA_KEY.is(dataId)) return SearchTabController.this;
            else return null;
        }
    }
}

