package com.samebug.clients.idea.ui.controller;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.samebug.clients.idea.ui.ImageUtil;
import com.samebug.clients.idea.ui.views.ExternalSolutionView;
import com.samebug.clients.idea.ui.views.SamebugTipView;
import com.samebug.clients.idea.ui.views.SearchGroupCardView;
import com.samebug.clients.idea.ui.views.SearchTabView;
import com.samebug.clients.search.api.entities.ComponentStack;
import com.samebug.clients.search.api.entities.ExceptionSearch;
import com.samebug.clients.search.api.entities.GroupedExceptionSearch;
import com.samebug.clients.search.api.entities.legacy.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.net.URL;
import java.util.ArrayList;

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

                if (model != null) {
                    view.header.add(new SearchGroupCardView(search).controlPanel);
                    for (final RestHit<Tip> tip : model.tips) {
                        view.solutionsPanel.add(new SamebugTipView(tip, model.breadcrumb).controlPanel);
                    }
                    for (final RestHit<SolutionReference> s : model.references) {
                        view.solutionsPanel.add(new ExternalSolutionView(s, model.breadcrumb).controlPanel);
                    }

                    view.controlPanel.revalidate();
                    view.controlPanel.repaint();
                }
            }
        });
    }
}
