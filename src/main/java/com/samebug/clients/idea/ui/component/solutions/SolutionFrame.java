package com.samebug.clients.idea.ui.component.solutions;

import com.intellij.util.messages.MessageBus;
import com.samebug.clients.idea.ui.ColorUtil;
import com.samebug.clients.idea.ui.component.profile.ProfilePanel;

import javax.swing.*;
import java.awt.*;

public final class SolutionFrame extends JPanel {
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

    @Override
    public void updateUI() {
        super.updateUI();
        setBackground(ColorUtil.background());
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

    public static final class Model {
        private final ExceptionHeaderPanel.Model header;
        private final ResultTabs.Model resultTabs;
        private final ProfilePanel.Model profilePanel;

        public Model(Model rhs) {
            this(rhs.resultTabs, rhs.header, rhs.profilePanel);
        }

        public Model(ResultTabs.Model resultTabs, ExceptionHeaderPanel.Model header, ProfilePanel.Model profilePanel) {
            this.resultTabs = resultTabs;
            this.header = header;
            this.profilePanel = profilePanel;
        }
    }
}
