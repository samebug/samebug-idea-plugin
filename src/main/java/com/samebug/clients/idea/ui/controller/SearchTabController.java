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

import com.intellij.ide.BrowserUtil;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.popup.Balloon;
import com.intellij.ui.awt.RelativePoint;
import com.samebug.clients.idea.components.application.IdeaClientService;
import com.samebug.clients.idea.components.application.IdeaSamebugPlugin;
import com.samebug.clients.idea.components.application.Tracking;
import com.samebug.clients.idea.components.project.TutorialProjectComponent;
import com.samebug.clients.idea.messages.ConnectionStatusListener;
import com.samebug.clients.idea.resources.SamebugBundle;
import com.samebug.clients.idea.resources.SamebugIcons;
import com.samebug.clients.idea.tracking.Events;
import com.samebug.clients.idea.ui.ImageUtil;
import com.samebug.clients.idea.ui.UrlUtil;
import com.samebug.clients.idea.ui.layout.EmptyWarningPanel;
import com.samebug.clients.idea.ui.views.ExternalSolutionView;
import com.samebug.clients.idea.ui.views.SamebugTipView;
import com.samebug.clients.idea.ui.views.SearchGroupCardView;
import com.samebug.clients.idea.ui.views.SearchTabView;
import com.samebug.clients.idea.ui.views.components.MarkPanel;
import com.samebug.clients.idea.ui.views.components.TutorialPanel;
import com.samebug.clients.idea.ui.views.components.tip.WriteTip;
import com.samebug.clients.idea.ui.views.components.tip.WriteTipHint;
import com.samebug.clients.search.api.entities.ComponentStack;
import com.samebug.clients.search.api.entities.ExceptionSearch;
import com.samebug.clients.search.api.entities.GroupedExceptionSearch;
import com.samebug.clients.search.api.entities.MarkResponse;
import com.samebug.clients.search.api.entities.legacy.*;
import com.samebug.clients.search.api.exceptions.BadRequest;
import com.samebug.clients.search.api.exceptions.SamebugClientException;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by poroszd on 3/29/16.
 */
public class SearchTabController {
    final Project project;
    final static Logger LOGGER = Logger.getInstance(SearchTabController.class);
    final SearchTabView view;
    final ConnectionStatusUpdater connectionStatusUpdater;

    @Nullable
    Solutions model;
    @Nullable
    GroupedExceptionSearch search;

    public SearchTabController(Project project) {
        this.project = project;
        view = new SearchTabView();
        connectionStatusUpdater = new ConnectionStatusUpdater();
    }

    public ConnectionStatusUpdater getStatusUpdater() {
        return connectionStatusUpdater;
    }

    public JPanel getControlPanel() {
        return view.controlPanel;
    }

    public void update(final Solutions solutions) {
        model = solutions;
        if (model != null) {
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
                                        crashDocUrl = UrlUtil.getCrashdocUrl(b);
                                        name = b.component.shortName;
                                        shortName = b.component.shortName;
                                    }
                                });
                            }

                        }
                    };
                }
            };
            ApplicationManager.getApplication().executeOnPooledThread(new Runnable() {
                @Override
                public void run() {
                    java.util.List<URL> imageUrls = new ArrayList<URL>();
                    for (final RestHit<Tip> tip : model.tips) {
                        imageUrls.add(tip.solution.author.avatarUrl);
                    }
                    for (final RestHit<SolutionReference> s : model.references) {
                        imageUrls.add(UrlUtil.getSourceIconUrl(s.solution.source.icon));
                    }

                    ImageUtil.loadImages(imageUrls);
                    refreshPane();
                }
            });
        } else {
            search = null;
        }
        refreshPane();
    }

    public void refreshPane() {
        ApplicationManager.getApplication().invokeLater(new Runnable() {
            public void run() {
                view.solutionsPanel.removeAll();
                view.header.removeAll();

                repaintHeader();
                if (model != null && search != null) {
                    if (model.tips.size() + model.references.size() == 0) {
                        EmptyWarningPanel panel = new EmptyWarningPanel();
                        panel.label.setText(SamebugBundle.message("samebug.toolwindow.search.content.empty"));
                        view.solutionsPanel.add(panel.controlPanel);
                    } else {
                        for (final RestHit<Tip> tip : model.tips) {
                            SamebugTipView tipView = new SamebugTipView(tip, model.breadcrumb, model.searchGroup._id.stackId);
                            view.solutionsPanel.add(tipView);
                            final MarkHandler markHandler = new MarkHandler(search.lastSearch.searchId, tip, tipView.markPanel);
                            tipView.markPanel.markButton.addMouseListener(markHandler);
                            tipView.writeBetter.addMouseListener(new WriteTipHandler());
                        }
                        for (final RestHit<SolutionReference> s : model.references) {
                            final ExternalSolutionView sv = new ExternalSolutionView(s, model.breadcrumb, model.searchGroup._id.stackId);
                            view.solutionsPanel.add(sv);
                            final MarkHandler markHandler = new MarkHandler(search.lastSearch.searchId, s, sv.markPanel);
                            sv.markPanel.markButton.addMouseListener(markHandler);
                        }
                    }

                    view.controlPanel.revalidate();
                    view.controlPanel.repaint();
                    if (model.references.size() + model.tips.size() > 0) TutorialProjectComponent.withTutorialProject(project, new SearchTabTutorial(model.tips.size() > 0));

                } else {
                    EmptyWarningPanel panel = new EmptyWarningPanel();
                    panel.label.setText(SamebugBundle.message("samebug.toolwindow.search.content.notConnected", UrlUtil.getServerRoot()));
                    view.solutionsPanel.add(panel.controlPanel);
                }
                view.controlPanel.revalidate();
                view.controlPanel.repaint();
            }
        });
    }

    void repaintHeader() {
        final SearchGroupCardView searchCard;
        final WriteTipHint writeTipHint;
        if (search != null) {
            searchCard = new SearchGroupCardView(search);
            searchCard.titleLabel.addMouseListener(new OpenSearchHandler());
        } else {
            searchCard = null;
        }

        if (model != null && model.tips.size() == 0) {
            writeTipHint = new WriteTipHint();
            writeTipHint.ctaButton.addMouseListener(new WriteTipHandler());
            // TODO add third case for preview
        } else {
            writeTipHint = null;
        }
        view.makeHeader(searchCard, writeTipHint);
        view.header.revalidate();
        view.header.repaint();
    }

    class OpenSearchHandler extends MouseAdapter {
        @Override
        public void mouseClicked(MouseEvent e) {
            URL url = UrlUtil.getSearchUrl(search.lastSearch.searchId);
            BrowserUtil.browse(url);
            Tracking.projectTracking(project).trace(Events.linkClick(project, url));
        }
    }

    class WriteTipHandler extends MouseAdapter {
        public WriteTipHandler() {
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            final WriteTip writeTip = new WriteTip();
            final SearchGroupCardView searchCard = new SearchGroupCardView(search);
            writeTip.cancel.addMouseListener(new TipCancelHandler());
            writeTip.submit.addMouseListener(new TipSubmitHandler(search.lastSearch.searchId, writeTip));
            view.makeHeader(searchCard, writeTip);
            view.header.revalidate();
            view.header.repaint();
        }
    }

    class TipCancelHandler extends MouseAdapter {
        @Override
        public void mouseClicked(MouseEvent e) {
            repaintHeader();
        }
    }

    class TipSubmitHandler extends MouseAdapter {
        final int searchId;
        final WriteTip tipPanel;

        public TipSubmitHandler(final int searchId, final WriteTip tipPanel) {
            this.searchId = searchId;
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
                        result = showError(SamebugBundle.message("samebug.tip.write.error.tip.short"));
                    } else if (tip.length() > WriteTip.maxCharacters) {
                        result = showError(SamebugBundle.message("samebug.tip.write.error.tip.long"));
                    } else if (StringUtils.countMatches(tip, System.lineSeparator()) >= WriteTip.maxLines) {
                        result = showError(SamebugBundle.message("samebug.tip.write.error.tip.tooManyLines"));
                    } else {
                        URL sourceUrl = null;
                        if (rawSourceUrl != null && !rawSourceUrl.trim().isEmpty()) {
                            try {
                                sourceUrl = new URL(rawSourceUrl);
                            } catch (MalformedURLException e1) {
                                result = showError(SamebugBundle.message("samebug.tip.write.error.source.malformed"));
                            }
                        }

                        if (result == null) {
                            IdeaClientService client = IdeaSamebugPlugin.getInstance().getClient();
                            try {
                                final RestHit<Tip> newTip = client.postTip(searchId, tip, sourceUrl);
                                model.tips.add(newTip);
                                refreshPane();
                                result = success();
                            } catch (final BadRequest e) {
                                final String errorMessageKey;
                                final String markErrorCode = e.getRestError().code;
                                if ("UNRECOGNIZED_SOURCE".equals(markErrorCode)) errorMessageKey = "samebug.tip.write.error.source.malformed";
                                else if ("MESSAGE_TOO_SHORT".equals(markErrorCode)) errorMessageKey = "samebug.tip.write.error.tip.short";
                                else if ("MESSAGE_TOO_LONG".equals(markErrorCode)) errorMessageKey = "samebug.tip.write.error.tip.long";
                                else if ("NOT_YOUR_SEARCH".equals(markErrorCode)) errorMessageKey = "samebug.tip.write.error.notYourSearch";
                                else if ("NOT_EXCEPTION_SEARCH".equals(markErrorCode)) errorMessageKey = "samebug.tip.write.error.notExceptionSearch";
                                else if ("UNKNOWN_SEARCH".equals(markErrorCode)) errorMessageKey = "samebug.tip.write.error.unknownSearch";
                                else errorMessageKey = "samebug.tip.write.error.source.unhandledBadRequest";
                                result = showError(SamebugBundle.message(errorMessageKey));
                            } catch (final SamebugClientException e) {
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

    class MarkHandler extends MouseAdapter {
        final int searchId;
        final RestHit hit;
        final MarkPanel markPanel;

        public MarkHandler(final int searchId, RestHit hit, final MarkPanel markPanel) {
            this.searchId = searchId;
            this.hit = hit;
            this.markPanel = markPanel;
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            markPanel.beginPostMark();
            ApplicationManager.getApplication().executeOnPooledThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        IdeaClientService client = IdeaSamebugPlugin.getInstance().getClient();
                        if (hit.markId == null) {
                            final MarkResponse mark = client.postMark(searchId, hit.solutionId);
                            hit.markId = mark.id;
                            hit.score = mark.marks;
                        } else {
                            final MarkResponse mark = client.retractMark(hit.markId);
                            hit.markId = null;
                            hit.score = mark.marks;
                        }
                        ApplicationManager.getApplication().invokeLater(new Runnable() {
                            @Override
                            public void run() {
                                markPanel.finishPostMarkWithSuccess(hit.score, hit.markId != null);
                            }
                        });
                    } catch (final BadRequest e) {
                        final String errorMessageKey;
                        final String markErrorCode = e.getRestError().code;
                        if ("ALREADY_MARKED".equals(markErrorCode)) errorMessageKey = "samebug.mark.error.alreadyMarked";
                        else if ("NOT_YOUR_SEARCH".equals(markErrorCode)) errorMessageKey = "samebug.mark.error.notYourSearch";
                        else if ("NOT_YOUR_MARK".equals(markErrorCode)) errorMessageKey = "samebug.mark.error.notYourMark";
                        else if ("ALREADY_CANCELLED".equals(markErrorCode)) errorMessageKey = "samebug.mark.error.alreadyCancelled";
                        else errorMessageKey = "samebug.mark.error.unhandledBadRequest";
                        ApplicationManager.getApplication().invokeLater(new Runnable() {
                            @Override
                            public void run() {
                                markPanel.finishPostMarkWithError(SamebugBundle.message(errorMessageKey));
                            }
                        });

                    } catch (final SamebugClientException e) {
                        ApplicationManager.getApplication().invokeLater(new Runnable() {
                            @Override
                            public void run() {
                                markPanel.finishPostMarkWithError(SamebugBundle.message("samebug.mark.error.unhandled"));
                            }
                        });
                    }
                }
            });

        }
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
                            view.statusIcon.setToolTipText(SamebugBundle.message("samebug.toolwindow.history.connectionStatus.description.notConnected", UrlUtil.getServerRoot()));
                        }
                        view.statusIcon.repaint();
                    }
                }
            });
        }
    }

    class SearchTabTutorial extends TutorialProjectComponent.TutorialProjectAnonfun<Void> {
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

