package com.samebug.clients.idea.ui.component.util.multiline;

import com.samebug.clients.idea.ui.ColorUtil;
import com.samebug.clients.idea.ui.FontRegistry;

import javax.swing.*;
import javax.swing.plaf.basic.BasicTextPaneUI;
import java.awt.*;

public class StyledMultilineLabel extends JTextPane {
    private Color[] foregroundColors;
    private Color[] backgroundColors;

    {
        setEditable(false);
        setCursor(null);
        setFocusable(false);
        setFont(new Font(FontRegistry.AvenirRegular, Font.PLAIN, 16));
        setForeground(ColorUtil.Text);
        setOpaque(false);
    }

    public void setForeground(Color[] c) {
        foregroundColors = c;
        setForeground(ColorUtil.forCurrentTheme(foregroundColors));
    }

    public void setBackground(Color[] c) {
        backgroundColors = c;
        setBackground(ColorUtil.forCurrentTheme(backgroundColors));
    }

    @Override
    public void updateUI() {
        setUI(new BasicTextPaneUI());
        setForeground(ColorUtil.forCurrentTheme(foregroundColors));
        setBackground(ColorUtil.forCurrentTheme(backgroundColors));
    }
}
