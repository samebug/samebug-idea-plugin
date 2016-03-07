package com.samebug.clients.idea.ui.controller;

import com.intellij.openapi.diagnostic.Logger;
import com.samebug.clients.idea.ui.views.SearchGroupCardView;
import com.samebug.clients.search.api.entities.GroupedExceptionSearch;

import javax.swing.*;

/**
 * Created by poroszd on 3/4/16.
 */
public class SearchGroupCardController {
    final private static Logger LOGGER = Logger.getInstance(SearchGroupCardController.class);
    final private SearchGroupCardView view;

    public SearchGroupCardController() {
        view = new SearchGroupCardView();
    }

    public void show(GroupedExceptionSearch model) {
        view.setHits(model.numberOfSolutions);
        view.setLastSeen(model.lastSeenSimilar);
        view.setContent(model.exception);
    }

    public JPanel getControlPanel() {
        return view.controlPanel;
    }
}
