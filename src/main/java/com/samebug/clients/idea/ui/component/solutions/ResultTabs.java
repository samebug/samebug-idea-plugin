package com.samebug.clients.idea.ui.component.solutions;

import com.intellij.util.messages.MessageBus;
import com.samebug.clients.common.ui.component.solutions.IResultTabs;
import com.samebug.clients.idea.ui.SamebugBundle;
import com.samebug.clients.idea.ui.component.util.tabbedPane.SamebugTabHeader;
import com.samebug.clients.idea.ui.component.util.tabbedPane.SamebugTabbedPane;

public final class ResultTabs extends SamebugTabbedPane implements IResultTabs {
    private final MessageBus messageBus;
    private final Model model;

    private final WebResultsTab webResultsTab;
    private final TipResultsTab tipResultsTab;
    private final SamebugTabHeader webResultsTabHeader;
    private final SamebugTabHeader tipResultsTabHeader;

    public ResultTabs(MessageBus messageBus, Model model) {
        this.model = new Model(model);
        this.messageBus = messageBus;

        webResultsTab = new WebResultsTab(messageBus, model.webResults, model.cta);
        tipResultsTab = new TipResultsTab(messageBus, model.tipResults, model.cta);

        tipResultsTabHeader = addTab(SamebugBundle.message("samebug.component.solutionFrame.tips.tabName"), model.tipResults.getTipsSize(), tipResultsTab);
        webResultsTabHeader = addTab(SamebugBundle.message("samebug.component.solutionFrame.webSolutions.tabName"), model.webResults.getHitsSize(), webResultsTab);
        tipResultsTabHeader.setSelected(true);
    }
}
