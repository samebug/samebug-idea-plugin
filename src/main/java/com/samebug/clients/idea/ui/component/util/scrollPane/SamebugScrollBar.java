package com.samebug.clients.idea.ui.component.util.scrollPane;

import javax.swing.*;

public class SamebugScrollBar extends JScrollBar {
    public SamebugScrollBar(int orientation) {
        super(orientation);
        setUnitIncrement(20);
    }

    @Override
    public void updateUI() {
        super.updateUI();
        setUI(new SamebugScrollBarUI());
    }
}
