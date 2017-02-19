package com.samebug.clients.idea.ui.component.solutions;

import com.intellij.util.messages.MessageBus;
import com.samebug.clients.common.ui.component.solutions.ISolutionFrame;
import com.samebug.clients.idea.ui.component.profile.ProfilePanel;
import com.samebug.clients.idea.ui.component.util.panel.SamebugPanel;

import javax.swing.*;
import java.awt.*;

public final class SolutionFrame extends SamebugPanel implements ISolutionFrame {
    private Solutions solutions;
    private final MessageBus messageBus;

    public SolutionFrame(MessageBus messageBus) {
        this.messageBus = messageBus;

        setLayout(new BorderLayout());
        setWarningLoading();
    }

    public void setWarningLoading() {
        // TODO
        add(new JLabel("loading..."));
    }

    public void setContent(Model model) {
        solutions = new Solutions(model);
        removeAll();
        add(solutions.exceptionHeader, BorderLayout.NORTH);
        add(solutions.tabs, BorderLayout.CENTER);
        add(solutions.profilePanel, BorderLayout.SOUTH);
        revalidate();
        repaint();
    }

    private final class Solutions {
        private final Model model;
        private final JPanel exceptionHeader;
        private final ResultTabs tabs;
        private final JPanel profilePanel;

        Solutions(Model model) {
            exceptionHeader = new ExceptionHeaderPanel(messageBus, model.header);
            tabs = new ResultTabs(messageBus, model.resultTabs);
            profilePanel = new ProfilePanel(messageBus, model.profilePanel);
            this.model = model;
        }
    }
}
