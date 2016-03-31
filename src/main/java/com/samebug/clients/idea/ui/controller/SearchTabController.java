package com.samebug.clients.idea.ui.controller;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.samebug.clients.idea.components.application.IdeaClientService;
import com.samebug.clients.idea.components.application.IdeaSamebugPlugin;
import com.samebug.clients.idea.ui.views.SamebugTipView;
import com.samebug.clients.idea.ui.views.SearchTabView;
import com.samebug.clients.search.api.entities.Tip;
import com.samebug.clients.search.api.entities.Solution;
import com.samebug.clients.search.api.entities.Solutions;
import com.samebug.clients.search.api.exceptions.SamebugClientException;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/**
 * Created by poroszd on 3/29/16.
 */
public class SearchTabController {
    final private Project project;
    final private static Logger LOGGER = Logger.getInstance(SearchTabController.class);
    final private SearchTabView view;
    @Nullable
    private Solutions model;

    public SearchTabController(Project project) {
        this.project = project;
        view = new SearchTabView();
    }

    public JPanel getControlPanel() {
        return view.controlPanel;
    }

    public void load(final int searchId) {
        ApplicationManager.getApplication().executeOnPooledThread(new Runnable() {
            @Override
            public void run() {
                IdeaClientService client = IdeaSamebugPlugin.getInstance().getClient();
                try {
                    model = client.getSolutions(searchId);
                    view.contentPanel.removeAll();
                    for (Tip tip : model.tips) {
                        view.contentPanel.add(new SamebugTipView(tip).controlPanel);
                    }
                } catch (SamebugClientException e1) {
                    LOGGER.warn("Failed to retrieve history", e1);
                }
            }
        });

    }

}
