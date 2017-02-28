package com.samebug.clients.swing.ui.component.util.scrollPane;

import com.samebug.clients.swing.ui.ColorUtil;

import javax.swing.*;
import java.awt.*;

public class SamebugScrollBar extends JScrollBar {
    private final Color[] Background = ColorUtil.Background;

    public SamebugScrollBar(int orientation) {
        super(orientation);
        setUnitIncrement(20);
        updateUI();
    }

    @Override
    public void updateUI() {
        setUI(new SamebugScrollBarUI());
        setBackground(ColorUtil.forCurrentTheme(Background));
    }
}
