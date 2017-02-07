package com.samebug.clients.idea.ui.component.experimental;

import com.intellij.util.messages.MessageBus;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;

public class SolutionFrame extends JPanel {
    Model model;

    @NotNull
    final JPanel exceptionHeader;
    @NotNull
    final ResultTabs tabs;
    @NotNull
    final JPanel profilePanel;
    @NotNull
    final MessageBus messageBus;

    public SolutionFrame(MessageBus messageBus) {
        exceptionHeader = new ExceptionHeaderPanel();
        tabs = new ResultTabs(messageBus);
        profilePanel = new ProfilePanel();
        this.messageBus = messageBus;

        setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        setLayout(new BorderLayout());
        setBackground(Color.white);

        add(exceptionHeader, BorderLayout.NORTH);
        add(tabs, BorderLayout.CENTER);
        add(profilePanel, BorderLayout.SOUTH);
    }

    public void setWarningLoading() {
        // TODO
    }

    public void update(Model model) {
        tabs.update(model.resultTabs);
    }

    public static final class Model {
        public ResultTabs.Model resultTabs;

        public Model(Model rhs) {
            this(rhs.resultTabs);
        }

        public Model(ResultTabs.Model resultTabs) {
            this.resultTabs = resultTabs;
        }
    }
}
