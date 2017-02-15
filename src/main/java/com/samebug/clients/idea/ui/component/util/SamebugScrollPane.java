package com.samebug.clients.idea.ui.component.util;

import javax.swing.*;
import javax.swing.plaf.basic.BasicScrollPaneUI;

public class SamebugScrollPane extends JScrollPane {
    {
        getVerticalScrollBar().setUnitIncrement(10);
        setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
    }

    @Override
    public void updateUI() {
        setUI(new BasicScrollPaneUI());
        setBorder(null);
    }
}
