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
import com.samebug.clients.idea.components.application.IdeaSamebugPlugin;
import com.samebug.clients.idea.components.application.Tracking;
import com.samebug.clients.idea.resources.SamebugBundle;
import com.samebug.clients.idea.tracking.Events;
import com.samebug.clients.idea.ui.ImageUtil;
import com.samebug.clients.idea.ui.layout.EmptyWarningPanel;
import com.samebug.clients.idea.ui.views.*;
import com.samebug.clients.idea.ui.views.components.tip.WriteTipCTA;
import com.samebug.clients.idea.ui.views.components.tip.WriteTipHint;
import com.samebug.clients.idea.ui.views.components.tip.WriteTip;
import com.samebug.clients.search.api.SamebugClient;
import com.samebug.clients.search.api.entities.ComponentStack;
import com.samebug.clients.search.api.entities.ExceptionSearch;
import com.samebug.clients.search.api.entities.GroupedExceptionSearch;
import com.samebug.clients.search.api.entities.legacy.*;
import com.samebug.clients.search.api.exceptions.SamebugClientException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.RunnableFuture;

/**
 * Created by poroszd on 3/29/16.
 */
public class SearchTabController {
    final Project project;
    final static Logger LOGGER = Logger.getInstance(SearchTabController.class);
    final SearchTabView view;

    @Nullable
    Solutions model;
    @Nullable
    GroupedExceptionSearch search;

    public SearchTabController(Project project) {
        this.project = project;
        view = new SearchTabView();
    }

    public JPanel getControlPanel() {
        return view.controlPanel;
    }

    public void update(@NotNull final Solutions solutions) {
        model = solutions;
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
                                    crashDocUrl = b.detailsUrl;
                                    name = b.component.shortName;
                                    shortName = b.component.shortName;
                                }
                            });
                        }

                    }
                };
            }
        };
        refreshPane();
        ApplicationManager.getApplication().executeOnPooledThread(new Runnable() {
            @Override
            public void run() {
                java.util.List<URL> imageUrls = new ArrayList<URL>();
                for (final RestHit<Tip> tip : model.tips) {
                    imageUrls.add(tip.solution.author.avatarUrl);
                }
                for (final RestHit<SolutionReference> s : model.references) {
                    imageUrls.add(s.solution.source.iconUrl);
                }

                ImageUtil.loadImages(imageUrls);
                refreshPane();
            }
        });
    }

    public void refreshPane() {
        ApplicationManager.getApplication().invokeLater(new Runnable() {
            public void run() {
                view.solutionsPanel.removeAll();
                view.header.removeAll();

                if (model != null && search != null) {
                    repaintHeader();

                    if (model.tips.size() + model.references.size() == 0) {
                        EmptyWarningPanel panel = new EmptyWarningPanel();
                        panel.label.setText(SamebugBundle.message("samebug.toolwindow.search.content.empty"));
                        view.solutionsPanel.add(panel.controlPanel);
                    } else {
                        for (final RestHit<Tip> tip : model.tips) {
                            view.solutionsPanel.add(new SamebugTipView(tip, model.breadcrumb));
                        }
                        for (final RestHit<SolutionReference> s : model.references) {
                            view.solutionsPanel.add(new ExternalSolutionView(s, model.breadcrumb));
                        }
                    }

                    view.controlPanel.revalidate();
                    view.controlPanel.repaint();
                } else {
                    EmptyWarningPanel panel = new EmptyWarningPanel();
                    panel.label.setText(SamebugBundle.message("samebug.toolwindow.search.content.notConnected", SamebugClient.root));
                    view.solutionsPanel.add(panel.controlPanel);
                }
                view.controlPanel.revalidate();
                view.controlPanel.repaint();
            }
        });
    }

    void repaintHeader() {
        final SearchGroupCardView searchCard = new SearchGroupCardView(search, new SearchGroupCardView.ActionHandler() {
            @Override
            public void onTitleClick() {
                URL url = SamebugClient.getSearchUrl(search.lastSearch.searchId);
                BrowserUtil.browse(url);
                Tracking.projectTracking(project).trace(Events.linkClick(project, url));
            }
        });
        // TODO write tip feature disabled
        if (false && model.tips.size() == 0) {
            final WriteTipHint writeTipHint = new WriteTipHint();
            writeTipHint.setActionHandler(CTAHandler(searchCard, writeTipHint));
            view.makeHeader(searchCard, writeTipHint);
            // TODO add third case for preview
        } else {
            view.makeHeader(searchCard, null);
        }
        view.header.revalidate();
        view.header.repaint();
    }


    WriteTipCTA.ActionHandler CTAHandler(final SearchGroupCardView searchCard, final WriteTipCTA writeTipCTA) {
        return writeTipCTA.new ActionHandler() {
            @Override
            public void onCTAClick() {
                final WriteTip writeTip = new WriteTip();
                writeTip.setActionHandler(tipActionHandler(writeTip));
                view.makeHeader(searchCard, writeTip);
                view.header.revalidate();
                view.header.repaint();
            }
        };
    }

    WriteTip.ActionHandler tipActionHandler(final WriteTip writeTip) {
        assert(search != null);
        assert(model != null);

        return writeTip.new ActionHandler() {
            @Override
            public void onCancel() {
                repaintHeader();
            }

            @Override
            public void onSubmit(final String tip, final URL sourceUrl) {
                ApplicationManager.getApplication().executeOnPooledThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            final RestHit<Tip> newTip = IdeaSamebugPlugin.getInstance().getClient().postTip(search.lastSearch.searchId, tip, sourceUrl);
                            success();
                            model.tips.add(newTip);
                            refreshPane();
                        } catch (SamebugClientException e) {
                            error(e.getMessage());
                        } finally {
                            ready();
                        }
                    }
                });
            }
        };
    }
}
