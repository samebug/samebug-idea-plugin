package com.samebug.clients.idea.ui.component.util.multiline;

import com.samebug.clients.idea.ui.ColorUtil;
import com.samebug.clients.idea.ui.FontRegistry;

import javax.swing.*;
import javax.swing.plaf.basic.BasicTextAreaUI;
import java.awt.*;

public class SamebugMultilineLabel extends JTextArea {
    private Color[] foregroundColors;
    private Color[] backgroundColors;

    {
        setEditable(false);
        setCursor(null);
        setFocusable(false);
        setWrapStyleWord(true);
        setLineWrap(true);

        setForeground(ColorUtil.Text);
        setFont(FontRegistry.regular(16));
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
        setUI(new BasicTextAreaUI());
        setForeground(ColorUtil.forCurrentTheme(foregroundColors));
        setBackground(ColorUtil.forCurrentTheme(backgroundColors));
    }
}
