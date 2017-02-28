package com.samebug.clients.swing.ui.component.util.scrollPane;

import com.samebug.clients.swing.ui.ColorUtil;

import javax.swing.*;
import java.awt.*;

public class SamebugScrollPane extends JScrollPane {
    private final Color[] Background = ColorUtil.Background;

    {
        setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        updateUI();
    }

    @Override
    public JScrollBar createVerticalScrollBar() {
        return new SamebugScrollBar(Adjustable.VERTICAL);
    }

    @Override
    public JScrollBar createHorizontalScrollBar() {
        return new SamebugScrollBar(Adjustable.HORIZONTAL);
    }

    @Override
    public void updateUI() {
        super.updateUI();
        setBackground(ColorUtil.forCurrentTheme(Background));
    }
}
