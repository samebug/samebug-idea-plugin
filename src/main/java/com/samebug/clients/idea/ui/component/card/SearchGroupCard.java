package com.samebug.clients.idea.ui.component.card;

import com.samebug.clients.idea.ui.component.organism.GroupInfoPanel;
import com.samebug.clients.search.api.entities.SearchGroup;

import javax.swing.*;

public abstract class SearchGroupCard extends JPanel {
    public GroupInfoPanel groupInfoPanel;

    public SearchGroupCard(SearchGroup group) {
        this.groupInfoPanel = new GroupInfoPanel(group);
    }

    public void refreshDateLabels() {
        groupInfoPanel.refreshDateLabels(getSearchGroup());
    }

    abstract SearchGroup getSearchGroup();
}
