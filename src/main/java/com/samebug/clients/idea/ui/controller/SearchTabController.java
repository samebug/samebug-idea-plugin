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
import com.samebug.clients.idea.messages.client.SearchModelListener;
import com.samebug.clients.idea.messages.view.SearchGroupCardListener;
import com.samebug.clients.idea.messages.view.SearchTabsViewListener;
import com.samebug.clients.idea.messages.view.SearchViewListener;
import com.samebug.clients.idea.ui.BrowserUtil;
import com.samebug.clients.idea.ui.ImageUtil;
import com.samebug.clients.idea.ui.component.card.ExternalSolutionView;
import com.samebug.clients.idea.ui.component.card.SamebugTipView;
import com.samebug.clients.idea.ui.component.organism.MarkPanel;
import com.samebug.clients.idea.ui.component.tab.SearchTabView;
import com.samebug.clients.idea.ui.listeners.ConnectionStatusUpdater;
import com.samebug.clients.search.api.entities.*;
import com.samebug.clients.search.api.exceptions.SamebugClientException;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

final public class SearchTabController implements TabController,
        SearchModelListener, SearchViewListener, SearchGroupCardListener, SearchTabsViewListener {
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

    public SearchTabController(@NotNull ToolWindowController twc, @NotNull Project project, final int searchId) {
        this.twc = twc;
        this.project = project;
        view = new SearchTabView();
        connectionStatusUpdater = new ConnectionStatusUpdater(view.statusIcon);
        this.mySearchId = searchId;
        service = new SearchService(searchId);

        DataManager.registerDataProvider(view, new MyDataProvider());
        MessageBusConnection projectMessageBus = project.getMessageBus().connect(project);
        projectMessageBus.subscribe(SearchModelListener.TOPIC, this);
        projectMessageBus.subscribe(SearchViewListener.TOPIC, this);
        projectMessageBus.subscribe(SearchGroupCardListener.TOPIC, this);
        projectMessageBus.subscribe(SearchTabsViewListener.TOPIC, this);
        projectMessageBus.subscribe(ConnectionStatusUpdater.TOPIC, connectionStatusUpdater);

    }

    @NotNull
    public JPanel getControlPanel() {
        return view;
    }

    @Override
    public void start(final int searchId) {
        if (mySearchId == searchId) {
            ApplicationManager.getApplication().invokeLater(new Runnable() {
                @Override
                public void run() {
                    view.setWarningLoading();
                }
            });
        }
    }

    @Override
    public void success(final int searchId, final Solutions result) {
        if (mySearchId == searchId) {
            service.setSolutions(result);
            ApplicationManager.getApplication().invokeLater(new Runnable() {
                @Override
                public void run() {
                    refreshTab();
                }
            });
        }
    }

    @Override
    public void fail(final int searchId, final java.lang.Exception e) {
        if (mySearchId == searchId) {
            service.setSolutions(null);
            ApplicationManager.getApplication().invokeLater(new Runnable() {
                @Override
                public void run() {
                    refreshTab();
                }
            });
        }
    }

    @Override
    public void finish(final int searchId) {
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

    private SearchTabView.Model convertSolutions(@NotNull final Solutions solutions) {
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
    private MarkPanel.Model convertHit(final SearchGroup search, final RestHit hit) {
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

    private SamebugTipView.Model convertTip(final SearchGroup search, final RestHit<Tip> hit) {
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

    private ExternalSolutionView.Model convertReference(final SearchGroup search, final RestHit<SolutionReference> hit) {
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

    @Override
    public void titleClick(@NotNull TabController tab, SearchGroup searchGroup) {
        if (this == tab) {
            BrowserUtil.browse(IdeaSamebugPlugin.getInstance().getUrlBuilder().search(searchGroup.getLastSearch().id));
        }
    }

    @Override
    public void reloadActiveSearchTab(@NotNull TabController tab) {
        if (this == tab) {
            ApplicationManager.getApplication().assertIsDispatchThread();
            reload();
        }
    }

    private class MyDataProvider implements DataProvider {
        @Override
        public Object getData(@NonNls final String dataId) {
            if (ToolWindowController.DATA_KEY.is(dataId)) return SearchTabController.this;
            else return null;
        }
    }

    public void refreshPane() {
        // TODO download and refresh, rename to reload
        ApplicationManager.getApplication().assertIsDispatchThread();

        view.setSolutions(null);
//        view.solutionsPanel.removeAll();
//        view.header.removeAll();
//        if (this.model != null) {
//            if (this.model.tips.size() + this.model.references.size() == 0) {
//                EmptyWarningPanel panel = new EmptyWarningPanel();
//                panel.label.setText(SamebugBundle.message("samebug.toolwindow.search.content.empty"));
//                view.solutionsPanel.add(panel.controlPanel);
//            } else {
//                for (final RestHit<Tip> tip : this.model.tips) {
//                    SamebugTipView tipView = new SamebugTipView(tip, this.model.searchGroup, IdeaSamebugPlugin.getInstance().getState().userId);
//                    view.solutionsPanel.add(tipView);
//                    final MarkHandler markHandler = new MarkHandler(project, this.model.searchGroup.getLastSearch(), tip, tipView.markPanel);
//                    tipView.markPanel.markButton.addMouseListener(markHandler);
//                    tipView.writeBetter.addMouseListener(new WriteTipHandler(this.model.searchGroup));
//                }
//                for (final RestHit<SolutionReference> s : this.model.references) {
//                    final ExternalSolutionView sv = new ExternalSolutionView(s, this.model.searchGroup, IdeaSamebugPlugin.getInstance().getState().userId);
//                    view.solutionsPanel.add(sv);
//                    final MarkHandler markHandler = new MarkHandler(project, this.model.searchGroup.getLastSearch(), s, sv.markPanel);
//                    sv.markPanel.markButton.addMouseListener(markHandler);
//                }
//            }
//
//            if (this.model.searchGroup instanceof StackTraceSearchGroup) {
//                StackTraceSearchGroup group = (StackTraceSearchGroup) this.model.searchGroup;
//                StackTraceSearchGroupCard card = new StackTraceSearchGroupCard(group);
//                card.titleLabel.addMouseListener(new LinkOpener(IdeaSamebugPlugin.getInstance().getUrlBuilder().search(group.lastSearch.id)));
//                view.searchCard = card;
//            } else {
//                TextSearchGroup group = (TextSearchGroup) this.model.searchGroup;
//                TextSearchGroupCard card = new TextSearchGroupCard(group);
//                card.titleLabel.addMouseListener(new LinkOpener(IdeaSamebugPlugin.getInstance().getUrlBuilder().search(group.lastSearch.id)));
//                view.searchCard = card;
//            }
//            view.addCtaHandler(new WriteTipHandler(this.model.searchGroup));
//            view.showWriteTipHint();
//            view.addTipCancelHandler(new TipCancelHandler(this.model.searchGroup));
//            view.addTipSubmitHandler(new TipSubmitHandler(this.model.searchGroup, view.tipPanel));
//            if (this.model.references.size() + this.model.tips.size() > 0)
// TutorialProjectComponent.withTutorialProject(project, new SearchTabTutorial(this.model.tips.size() > 0));
//
//        } else {
//            EmptyWarningPanel panel = new EmptyWarningPanel();
//            panel.label.setText(SamebugBundle.message("samebug.toolwindow.search.content.notConnected", IdeaSamebugPlugin.getInstance().getUrlBuilder().getServerRoot()));
//            view.solutionsPanel.add(panel.controlPanel);
//        }
//        view.controlPanel.revalidate();
//        view.controlPanel.repaint();
    }

    // TODO organize these handlers
//    final public class WriteTipHandler extends MouseAdapter {
//        @NotNull
//        final SearchGroup searchGroup;
//
//        public WriteTipHandler(@NotNull SearchGroup searchGroup) {
//            this.searchGroup = searchGroup;
//        }
//
//        @Override
//        public void mouseClicked(MouseEvent e) {
//            Tracking.projectTracking(project).trace(Events.writeTipOpen(project, searchGroup.getLastSearch().id));
//            view.showWriteTip();
//            view.header.revalidate();
//            view.header.repaint();
//        }
//    }
//
//    final public class TipCancelHandler extends MouseAdapter {
//        @NotNull
//        final SearchGroup searchGroup;
//
//        public TipCancelHandler(@NotNull SearchGroup searchGroup) {
//            this.searchGroup = searchGroup;
//        }
//
//        @Override
//        public void mouseClicked(MouseEvent e) {
//            Tracking.projectTracking(project).trace(Events.writeTipCancel(project, searchGroup.getLastSearch().id));
//            view.showWriteTipHint();
//            view.header.revalidate();
//            view.header.repaint();
//        }
//    }
//
//    final public class TipSubmitHandler extends MouseAdapter {
//        @NotNull
//        final SearchGroup searchGroup;
//        @NotNull
//        final WriteTip tipPanel;
//
//        public TipSubmitHandler(@NotNull SearchGroup searchGroup, @NotNull WriteTip tipPanel) {
//            this.searchGroup = searchGroup;
//            this.tipPanel = tipPanel;
//        }
//
//        @Override
//        public void mouseClicked(MouseEvent e) {
//            tipPanel.beginPostTip();
//            ApplicationManager.getApplication().executeOnPooledThread(new Runnable() {
//                @Override
//                public void run() {
//                    Runnable result = null;
//                    final String tip = tipPanel.tipBody.getText();
//                    final String rawSourceUrl = tipPanel.sourceLink.getText();
//
//                    if (tip.length() < WriteTip.minCharacters) {
//                        Tracking.projectTracking(project).trace(
//                                Events.writeTipSubmit(project, searchGroup.getLastSearch().id, tip, rawSourceUrl, "samebug.tip.write.error.tip.short"));
//                        result = showError(SamebugBundle.message("samebug.tip.write.error.tip.short"));
//                    } else if (tip.length() > WriteTip.maxCharacters) {
//                        Tracking.projectTracking(project).trace(
//                                Events.writeTipSubmit(project, searchGroup.getLastSearch().id, tip, rawSourceUrl, "samebug.tip.write.error.tip.long"));
//                        result = showError(SamebugBundle.message("samebug.tip.write.error.tip.long"));
//                    } else if (StringUtils.countMatches(tip, TextUtil.lineSeparator) >= WriteTip.maxLines) {
//                        Tracking.projectTracking(project).trace(
//                                Events.writeTipSubmit(project, searchGroup.getLastSearch().id, tip, rawSourceUrl, "samebug.tip.write.error.tip.tooManyLines"));
//                        result = showError(SamebugBundle.message("samebug.tip.write.error.tip.tooManyLines"));
//                    } else {
//                        URL sourceUrl = null;
//                        if (rawSourceUrl != null && !rawSourceUrl.trim().isEmpty()) {
//                            try {
//                                sourceUrl = new URL(rawSourceUrl);
//                            } catch (MalformedURLException e1) {
//                                Tracking.projectTracking(project).trace(
//                                        Events.writeTipSubmit(project, searchGroup.getLastSearch().id, tip, rawSourceUrl, "samebug.tip.write.error.source.malformed"));
//                                result = showError(SamebugBundle.message("samebug.tip.write.error.source.malformed"));
//                            }
//                        }
//
//                        if (result == null) {
//                            ClientService client = IdeaSamebugPlugin.getInstance().getClient();
//                            try {
//                                final RestHit<Tip> newTip = client.postTip(searchGroup.getLastSearch().id, tip, sourceUrl);
//                                model.tips.add(newTip);
//                                Tracking.projectTracking(project).trace(
//                                        Events.writeTipSubmit(project, searchGroup.getLastSearch().id, tip, rawSourceUrl, Integer.toString(newTip.solutionId)));
//                                ApplicationManager.getApplication().invokeLater(new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        refreshPane();
//                                    }
//                                });
//                                result = success();
//                            } catch (final BadRequest e) {
//                                final String errorMessageKey;
//                                final String writeTipErrorCode = e.getRestError().code;
//                                if ("UNRECOGNIZED_SOURCE".equals(writeTipErrorCode)) errorMessageKey = "samebug.tip.write.error.source.malformed";
//                                else if ("MESSAGE_TOO_SHORT".equals(writeTipErrorCode)) errorMessageKey = "samebug.tip.write.error.tip.short";
//                                else if ("MESSAGE_TOO_LONG".equals(writeTipErrorCode)) errorMessageKey = "samebug.tip.write.error.tip.long";
//                                else if ("NOT_YOUR_SEARCH".equals(writeTipErrorCode)) errorMessageKey = "samebug.tip.write.error.notYourSearch";
//                                else if ("NOT_EXCEPTION_SEARCH".equals(writeTipErrorCode)) errorMessageKey = "samebug.tip.write.error.notExceptionSearch";
//                                else if ("UNKNOWN_SEARCH".equals(writeTipErrorCode)) errorMessageKey = "samebug.tip.write.error.unknownSearch";
//                                else errorMessageKey = "samebug.tip.write.error.source.unhandledBadRequest";
//                                Tracking.projectTracking(project).trace(
//                                        Events.writeTipSubmit(project, searchGroup.getLastSearch().id, tip, rawSourceUrl, errorMessageKey));
//                                result = showError(SamebugBundle.message(errorMessageKey));
//                            } catch (final SamebugClientException e) {
//                                Tracking.projectTracking(project).trace(
//                                        Events.writeTipSubmit(project, searchGroup.getLastSearch().id, tip, rawSourceUrl, "samebug.tip.write.error.source.unhandled"));
//                                result = showError(SamebugBundle.message("samebug.tip.write.error.source.unhandled"));
//                            }
//                        }
//                    }
//                    ApplicationManager.getApplication().invokeLater(result);
//                }
//            });
//
//        }
//
//        Runnable showError(final String message) {
//            return new Runnable() {
//                @Override
//                public void run() {
//                    tipPanel.finishPostTipWithError(message);
//                }
//            };
//        }
//
//        Runnable success() {
//            return new Runnable() {
//                @Override
//                public void run() {
//                    tipPanel.finishPostTipWithSuccess();
//                }
//            };
//        }
//    }
//
//    final class SearchTabTutorial extends TutorialProjectComponent.TutorialProjectAnonfun<Void> {
//        final boolean tipsShown;
//
//        public SearchTabTutorial(final boolean tipsShown) {
//            this.tipsShown = tipsShown;
//        }
//
//        @Override
//        public Void call() {
//            final JPanel tutorialPanel;
//            if (settings.searchTab) {
//                settings.searchTab = false;
//                if (tipsShown) {
//                    tutorialPanel = new TutorialPanel(SamebugBundle.message("samebug.tutorial.searchTab.tips.title"),
//                            SamebugBundle.message("samebug.tutorial.searchTab.tips.message"));
//                } else {
//                    tutorialPanel = new TutorialPanel(SamebugBundle.message("samebug.tutorial.searchTab.noTips.title"),
//                            SamebugBundle.message("samebug.tutorial.searchTab.noTips.message"));
//                }
//                Balloon balloon = TutorialProjectComponent.createTutorialBalloon(project, tutorialPanel);
//                balloon.show(RelativePoint.getNorthWestOf(view.toolbarPanel), Balloon.Position.atLeft);
//            }
//            return null;
//        }
//    }
}

