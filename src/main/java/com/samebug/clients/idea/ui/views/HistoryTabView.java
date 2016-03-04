package com.samebug.clients.idea.ui.views;

import javafx.scene.control.*;
import javafx.scene.control.ScrollPane;

import javax.swing.*;
import java.awt.*;

/**
 * Created by poroszd on 3/4/16.
 */
public class HistoryTabView {
    public JPanel controlPanel;
    public JPanel toolbarPanel;
    public JScrollPane scrollPane;
    public JPanel contentPanel;

    public HistoryTabView() {
        controlPanel = new JPanel();
        controlPanel.setLayout(new BorderLayout(0, 0));
        toolbarPanel = new JPanel();
        toolbarPanel.setLayout(new BorderLayout(0, 0));
        controlPanel.add(toolbarPanel, BorderLayout.NORTH);
        scrollPane = new JScrollPane();
        controlPanel.add(scrollPane, BorderLayout.CENTER);
        contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.PAGE_AXIS));
        scrollPane.setViewportView(contentPanel);

    }
}
