package com.samebug.clients.idea.ui.controller;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.samebug.clients.idea.ui.views.SearchGroupCardView;
import com.samebug.clients.search.api.entities.GroupedExceptionSearch;

import javax.swing.*;

/**
 * Created by poroszd on 3/4/16.
 */
public class SearchGroupCardController {
    final private static Logger LOGGER = Logger.getInstance(SearchGroupCardController.class);
    final private SearchGroupCardView view;
    final private GroupedExceptionSearch model;

    public SearchGroupCardController(GroupedExceptionSearch model, Project project) {
        view = new SearchGroupCardView(project);
        this.model = model;
        updateFields();
    }

    private void updateFields() {
        view.setContent(model);
    }

    public JPanel getControlPanel() {
        return view.controlPanel;
    }
}
