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
import com.intellij.ui.awt.RelativePoint;
import com.samebug.clients.idea.components.application.IdeaClientService;
import com.samebug.clients.idea.components.application.IdeaSamebugPlugin;
import com.samebug.clients.idea.components.application.Tracking;
import com.samebug.clients.idea.components.project.TutorialProjectComponent;
import com.samebug.clients.idea.resources.SamebugBundle;
import com.samebug.clients.idea.tracking.Events;
import com.samebug.clients.idea.ui.ImageUtil;
import com.samebug.clients.idea.ui.component.TutorialPanel;
import com.samebug.clients.idea.ui.component.WriteTip;
import com.samebug.clients.idea.ui.component.card.ExternalSolutionView;
import com.samebug.clients.idea.ui.component.card.SamebugTipView;
import com.samebug.clients.idea.ui.component.card.SearchGroupCardView;
import com.samebug.clients.idea.ui.component.tab.SearchTabView;
import com.samebug.clients.idea.ui.layout.EmptyWarningPanel;
import com.samebug.clients.idea.ui.listeners.ConnectionStatusUpdater;
import com.samebug.clients.idea.ui.listeners.LinkOpener;
import com.samebug.clients.idea.ui.listeners.MarkHandler;
import com.samebug.clients.search.api.entities.ComponentStack;
import com.samebug.clients.search.api.entities.ExceptionSearch;
import com.samebug.clients.search.api.entities.GroupedExceptionSearch;
import com.samebug.clients.search.api.entities.legacy.*;
import com.samebug.clients.search.api.exceptions.BadRequest;
import com.samebug.clients.search.api.exceptions.SamebugClientException;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

final public class SearchTabController {
    final static Logger LOGGER = Logger.getInstance(SearchTabController.class);
    @NotNull
    final Project project;
    @NotNull
    final SearchTabView view;
    @NotNull
    final ConnectionStatusUpdater connectionStatusUpdater;

    @Nullable
    Solutions model;
    // TODO this field is actually a transformed field from model. Get rid of this when possible.
    @Nullable
    GroupedExceptionSearch search;

    public SearchTabController(@NotNull Project project) {
        this.project = project;
        view = new SearchTabView();
        connectionStatusUpdater = new ConnectionStatusUpdater(view.statusIcon);
    }

    @NotNull
    public ConnectionStatusUpdater getStatusUpdater() {
        return connectionStatusUpdater;
    }

    @NotNull
    public JPanel getControlPanel() {
        return view.controlPanel;
    }

    public void update(@Nullable final Solutions solutions) {
        ApplicationManager.getApplication().assertIsDispatchThread();
        model = solutions;
        if (model != null) {
            // FIXME transform between the two search group entity. Get rid of this after the rest api is cleared.
            search = new GroupedExceptionSearch() {
                {
                    firstSeenSimilar = model.searchGroup.firstSeen;
                    lastSeenSimilar = model.searchGroup.lastSeen;
                    numberOfSimilars = model.searchGroup.numberOfSimilars;
                    numberOfSolutions = model.tips.size() + model.references.size();
                    lastSearch = new ExceptionSearch() {
                        {
                            searchId = model.search._id;
                            exception = model.search.exception;
                            componentStack = new ArrayList<ComponentStack>();
                            for (final BreadCrumb b : model.breadcrumb) {
                                componentStack.add(new ComponentStack() {
                                    {
                                        color = b.component.color;
                                        crashDocUrl = IdeaSamebugPlugin.getInstance().getUrlBuilder().crashdoc(b);
                                        name = b.component.shortName;
                                        shortName = b.component.shortName;
                                    }
                                });
                            }

                        }
                    };
                }
            };

            // Loading avatars to imageCache on a background thread, and reload them when all images are ready.
            ApplicationManager.getApplication().executeOnPooledThread(new Runnable() {
                @Override
                public void run() {
                    java.util.List<URL> imageUrls = new ArrayList<URL>();
                    try {
                        imageUrls.add(new URL(IdeaSamebugPlugin.getInstance().getState().avatarUrl));
                    } catch (Throwable e) {
                        LOGGER.warn("Failed to load user's avatar", e);
                    }
                    for (final RestHit<Tip> tip : model.tips) {
                        imageUrls.add(tip.solution.author.avatarUrl);
                    }
                    for (final RestHit<SolutionReference> s : model.references) {
                        imageUrls.add(IdeaSamebugPlugin.getInstance().getUrlBuilder().sourceIcon(s.solution.source.icon));
                    }

                    ImageUtil.loadImages(imageUrls);
                    ApplicationManager.getApplication().invokeLater(new Runnable() {
                        public void run() {
                            refreshPane();
                        }
                    });
                }
            });
        } else {
            search = null;
        }
        refreshPane();
    }

    public void refreshPane() {
        ApplicationManager.getApplication().assertIsDispatchThread();
        view.solutionsPanel.removeAll();
        view.header.removeAll();
        if (model != null && search != null) {
            if (model.tips.size() + model.references.size() == 0) {
                EmptyWarningPanel panel = new EmptyWarningPanel();
                panel.label.setText(SamebugBundle.message("samebug.toolwindow.search.content.empty"));
                view.solutionsPanel.add(panel.controlPanel);
            } else {
                for (final RestHit<Tip> tip : model.tips) {
                    SamebugTipView tipView = new SamebugTipView(tip, model.searchGroup, model.breadcrumb, IdeaSamebugPlugin.getInstance().getState().userId);
                    view.solutionsPanel.add(tipView);
                    final MarkHandler markHandler = new MarkHandler(project, search.lastSearch, tip, tipView.markPanel);
                    tipView.markPanel.markButton.addMouseListener(markHandler);
                    tipView.writeBetter.addMouseListener(new WriteTipHandler(search));
                }
                for (final RestHit<SolutionReference> s : model.references) {
                    final ExternalSolutionView sv = new ExternalSolutionView(s, model.searchGroup, model.breadcrumb, IdeaSamebugPlugin.getInstance().getState().userId);
                    view.solutionsPanel.add(sv);
                    final MarkHandler markHandler = new MarkHandler(project, search.lastSearch, s, sv.markPanel);
                    sv.markPanel.markButton.addMouseListener(markHandler);
                }
            }

            view.searchCard = new SearchGroupCardView(search);
            view.searchCard.titleLabel.addMouseListener(new LinkOpener(IdeaSamebugPlugin.getInstance().getUrlBuilder().search(search.lastSearch.searchId)));
            view.writeTipHint.ctaButton.addMouseListener(new WriteTipHandler(search));
            view.showWriteTipHint();
            view.tipPanel.cancel.addMouseListener(new TipCancelHandler(search));
            view.tipPanel.submit.addMouseListener(new TipSubmitHandler(search, view.tipPanel));
            if (model.references.size() + model.tips.size() > 0) TutorialProjectComponent.withTutorialProject(project, new SearchTabTutorial(model.tips.size() > 0));

        } else {
            EmptyWarningPanel panel = new EmptyWarningPanel();
            panel.label.setText(SamebugBundle.message("samebug.toolwindow.search.content.notConnected", IdeaSamebugPlugin.getInstance().getUrlBuilder().getServerRoot()));
            view.solutionsPanel.add(panel.controlPanel);
        }
        view.controlPanel.revalidate();
        view.controlPanel.repaint();
    }


    // TODO organize these handlers
    final public class WriteTipHandler extends MouseAdapter {
        @NotNull
        final GroupedExceptionSearch searchGroup;

        public WriteTipHandler(@NotNull GroupedExceptionSearch searchGroup) {
            this.searchGroup = searchGroup;
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            Tracking.projectTracking(project).trace(Events.writeTipOpen(project, searchGroup.lastSearch.searchId));
            view.showWriteTip();
            view.header.revalidate();
            view.header.repaint();
        }
    }

    final public class TipCancelHandler extends MouseAdapter {
        @NotNull
        final GroupedExceptionSearch searchGroup;

        public TipCancelHandler(@NotNull GroupedExceptionSearch searchGroup) {
            this.searchGroup = searchGroup;
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            Tracking.projectTracking(project).trace(Events.writeTipCancel(project, searchGroup.lastSearch.searchId));
            view.showWriteTipHint();
            view.header.revalidate();
            view.header.repaint();
        }
    }

    final public class TipSubmitHandler extends MouseAdapter {
        @NotNull
        final GroupedExceptionSearch searchGroup;
        @NotNull
        final WriteTip tipPanel;

        public TipSubmitHandler(@NotNull GroupedExceptionSearch searchGroup, @NotNull WriteTip tipPanel) {
            this.searchGroup = searchGroup;
            this.tipPanel = tipPanel;
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            tipPanel.beginPostTip();
            ApplicationManager.getApplication().executeOnPooledThread(new Runnable() {
                @Override
                public void run() {
                    Runnable result = null;
                    final String tip = tipPanel.tipBody.getText();
                    final String rawSourceUrl = tipPanel.sourceLink.getText();

                    if (tip.length() < WriteTip.minCharacters) {
                        Tracking.projectTracking(project).trace(
                                Events.writeTipSubmit(project, searchGroup.lastSearch.searchId, tip, rawSourceUrl, "samebug.tip.write.error.tip.short"));
                        result = showError(SamebugBundle.message("samebug.tip.write.error.tip.short"));
                    } else if (tip.length() > WriteTip.maxCharacters) {
                        Tracking.projectTracking(project).trace(
                                Events.writeTipSubmit(project, searchGroup.lastSearch.searchId, tip, rawSourceUrl, "samebug.tip.write.error.tip.long"));
                        result = showError(SamebugBundle.message("samebug.tip.write.error.tip.long"));
                    } else if (StringUtils.countMatches(tip, System.lineSeparator()) >= WriteTip.maxLines) {
                        Tracking.projectTracking(project).trace(
                                Events.writeTipSubmit(project, searchGroup.lastSearch.searchId, tip, rawSourceUrl, "samebug.tip.write.error.tip.tooManyLines"));
                        result = showError(SamebugBundle.message("samebug.tip.write.error.tip.tooManyLines"));
                    } else {
                        URL sourceUrl = null;
                        if (rawSourceUrl != null && !rawSourceUrl.trim().isEmpty()) {
                            try {
                                sourceUrl = new URL(rawSourceUrl);
                            } catch (MalformedURLException e1) {
                                Tracking.projectTracking(project).trace(
                                        Events.writeTipSubmit(project, searchGroup.lastSearch.searchId, tip, rawSourceUrl, "samebug.tip.write.error.source.malformed"));
                                result = showError(SamebugBundle.message("samebug.tip.write.error.source.malformed"));
                            }
                        }

                        if (result == null) {
                            IdeaClientService client = IdeaSamebugPlugin.getInstance().getClient();
                            try {
                                final RestHit<Tip> newTip = client.postTip(searchGroup.lastSearch.searchId, tip, sourceUrl);
                                model.tips.add(newTip);
                                Tracking.projectTracking(project).trace(
                                        Events.writeTipSubmit(project, searchGroup.lastSearch.searchId, tip, rawSourceUrl, Integer.toString(newTip.solutionId)));
                                ApplicationManager.getApplication().invokeLater(new Runnable() {
                                    @Override
                                    public void run() {
                                        refreshPane();
                                    }
                                });
                                result = success();
                            } catch (final BadRequest e) {
                                final String errorMessageKey;
                                final String writeTipErrorCode = e.getRestError().code;
                                if ("UNRECOGNIZED_SOURCE".equals(writeTipErrorCode)) errorMessageKey = "samebug.tip.write.error.source.malformed";
                                else if ("MESSAGE_TOO_SHORT".equals(writeTipErrorCode)) errorMessageKey = "samebug.tip.write.error.tip.short";
                                else if ("MESSAGE_TOO_LONG".equals(writeTipErrorCode)) errorMessageKey = "samebug.tip.write.error.tip.long";
                                else if ("NOT_YOUR_SEARCH".equals(writeTipErrorCode)) errorMessageKey = "samebug.tip.write.error.notYourSearch";
                                else if ("NOT_EXCEPTION_SEARCH".equals(writeTipErrorCode)) errorMessageKey = "samebug.tip.write.error.notExceptionSearch";
                                else if ("UNKNOWN_SEARCH".equals(writeTipErrorCode)) errorMessageKey = "samebug.tip.write.error.unknownSearch";
                                else errorMessageKey = "samebug.tip.write.error.source.unhandledBadRequest";
                                Tracking.projectTracking(project).trace(
                                        Events.writeTipSubmit(project, searchGroup.lastSearch.searchId, tip, rawSourceUrl, errorMessageKey));
                                result = showError(SamebugBundle.message(errorMessageKey));
                            } catch (final SamebugClientException e) {
                                Tracking.projectTracking(project).trace(
                                        Events.writeTipSubmit(project, searchGroup.lastSearch.searchId, tip, rawSourceUrl, "samebug.tip.write.error.source.unhandled"));
                                result = showError(SamebugBundle.message("samebug.tip.write.error.source.unhandled"));
                            }
                        }
                    }
                    ApplicationManager.getApplication().invokeLater(result);
                }
            });

        }

        Runnable showError(final String message) {
            return new Runnable() {
                @Override
                public void run() {
                    tipPanel.finishPostTipWithError(message);
                }
            };
        }

        Runnable success() {
            return new Runnable() {
                @Override
                public void run() {
                    tipPanel.finishPostTipWithSuccess();
                }
            };
        }
    }

    final class SearchTabTutorial extends TutorialProjectComponent.TutorialProjectAnonfun<Void> {
        final boolean tipsShown;

        public SearchTabTutorial(final boolean tipsShown) {
            this.tipsShown = tipsShown;
        }

        @Override
        public Void call() {
            final JPanel tutorialPanel;
            if (settings.searchTab) {
                settings.searchTab = false;
                if (tipsShown) {
                    tutorialPanel = new TutorialPanel(SamebugBundle.message("samebug.tutorial.searchTab.tips.title"),
                            SamebugBundle.message("samebug.tutorial.searchTab.tips.message"));
                } else {
                    tutorialPanel = new TutorialPanel(SamebugBundle.message("samebug.tutorial.searchTab.noTips.title"),
                            SamebugBundle.message("samebug.tutorial.searchTab.noTips.message"));
                }
                Balloon balloon = TutorialProjectComponent.createTutorialBalloon(project, tutorialPanel);
                balloon.show(RelativePoint.getNorthWestOf(view.toolbarPanel), Balloon.Position.atLeft);
            }
            return null;
        }
    }
}

