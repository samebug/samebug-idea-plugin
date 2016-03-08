package com.samebug.clients.idea.ui.layout;

import javax.swing.*;
import java.awt.*;

/**
 * Created by poroszd on 3/4/16.
 * <p/>
 * Manually modified:
 * - contentPanel should have BoxLayout (cannot set it in the designer)
 * - scrollPane.getVerticalScrollBar().setUnitIncrement(50);
 */
public class HistoryTab {
    private JPanel controlPanel;
    private JPanel toolbarPanel;
    private JScrollPane scrollPane;
    private JPanel contentPanel;
    public JLabel actionToolbar;
    public JLabel statusIcon;

}
