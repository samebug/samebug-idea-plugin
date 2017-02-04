package com.samebug.clients.idea.ui.component.experimental;

import javax.swing.*;
import java.awt.*;

public class ToolWindowPanel extends JPanel {
    JPanel exceptionHeader;
    JTabbedPane tabs;
    JPanel profilePanel;

    public ToolWindowPanel() {
        exceptionHeader = new ExceptionHeaderPanel();
        tabs = new ResultTabs();
        profilePanel = new ProfilePanel(null);

        setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        setLayout(new BorderLayout());
        setBackground(Color.white);

        add(exceptionHeader, BorderLayout.NORTH);
        add(tabs, BorderLayout.CENTER);
        add(profilePanel, BorderLayout.SOUTH);
    }

}
