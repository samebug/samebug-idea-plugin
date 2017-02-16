package com.samebug.clients.idea.ui.component.util.panel;

import com.samebug.clients.idea.ui.ColorUtil;

import javax.swing.*;
import java.awt.*;

public class Panel extends JPanel {
    private Color[] foregroundColors;
    private Color[] backgroundColors;

    public Panel() {
        backgroundColors = ColorUtil.Background;
        updateUI();
    }

    public void setBackground(Color[] c) {
        backgroundColors = c;
        super.setBackground(ColorUtil.forCurrentTheme(backgroundColors));
    }

    public void setForeground(Color[] c) {
        foregroundColors = c;
        super.setForeground(ColorUtil.forCurrentTheme(foregroundColors));
    }

    @Override
    public void updateUI() {
        super.updateUI();
        super.setForeground(ColorUtil.forCurrentTheme(foregroundColors));
        super.setBackground(ColorUtil.forCurrentTheme(backgroundColors));
    }
}
