package com.samebug.clients.idea.ui.controller;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.util.ui.components.BorderLayoutPanel;
import com.samebug.clients.idea.ui.views.ExternalSolutionView;
import com.samebug.clients.idea.ui.views.SamebugTipView;
import com.samebug.clients.idea.ui.views.SearchGroupCardView;
import com.samebug.clients.idea.ui.views.SearchTabView;
import com.samebug.clients.search.api.entities.ExternalSolution;
import com.samebug.clients.search.api.entities.Solutions;
import com.samebug.clients.search.api.entities.Tip;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;

/**
 * Created by poroszd on 3/29/16.
 */
public class SearchTabController {
    final Project project;
    final static Logger LOGGER = Logger.getInstance(SearchTabController.class);
    final SearchTabView view;

    @Nullable
    Solutions model;

    public SearchTabController(Project project) {
        this.project = project;
        view = new SearchTabView();
    }

    public JPanel getControlPanel() {
        return view.controlPanel;
    }

    public void update(final Solutions solutions) {
        model = solutions;
        refreshPane();
    }

    public void refreshPane() {
        ApplicationManager.getApplication().invokeLater(new Runnable() {
            public void run() {
                view.solutionsPanel.removeAll();

                if (model != null) {
                    view.header.add(new SearchGroupCardView(model.search).controlPanel);
                    for (final Tip tip : model.tips) {
                        view.solutionsPanel.add(new SamebugTipView(tip).controlPanel);
                    }
                    for (final ExternalSolution s : model.externalSolutions) {
                        view.solutionsPanel.add(new ExternalSolutionView(s).controlPanel);
                    }

                    view.controlPanel.revalidate();
                    view.controlPanel.repaint();
                }
            }
        });
    }
}
