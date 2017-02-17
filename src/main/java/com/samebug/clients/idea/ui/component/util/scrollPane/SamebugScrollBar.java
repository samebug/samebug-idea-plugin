package com.samebug.clients.idea.ui.component.util.scrollPane;

import com.samebug.clients.idea.ui.ColorUtil;

import javax.swing.*;
import java.awt.*;

public class SamebugScrollBar extends JScrollBar {
    private Color[] backgroundColors;

    public SamebugScrollBar(int orientation) {
        super(orientation);
        setUnitIncrement(20);
        setBackground(ColorUtil.Background);
        updateUI();
    }

    public void setBackground(Color[] c) {
        backgroundColors = c;
        super.setBackground(ColorUtil.forCurrentTheme(backgroundColors));
    }

    @Override
    public void updateUI() {
        super.updateUI();
        setUI(new SamebugScrollBarUI());
        super.setBackground(ColorUtil.forCurrentTheme(backgroundColors));
    }
}
