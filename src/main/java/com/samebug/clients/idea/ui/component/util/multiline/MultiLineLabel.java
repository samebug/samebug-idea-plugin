package com.samebug.clients.idea.ui.component.util.multiline;

import com.samebug.clients.idea.ui.ColorUtil;
import com.samebug.clients.idea.ui.FontRegistry;

import javax.swing.*;
import java.awt.*;

public class MultiLineLabel extends JTextArea {
    private Color[] foregroundColors;
    private Color[] backgroundColors;

    {
        setEditable(false);
        setCursor(null);
        setFocusable(false);
        setWrapStyleWord(true);
        setLineWrap(true);

        setForeground(ColorUtil.Text);
        setFont(new Font(FontRegistry.AvenirRegular, Font.PLAIN, 16));
        setOpaque(false);
    }

    public void setForeground(Color[] c) {
        foregroundColors = c;
        super.setForeground(ColorUtil.forCurrentTheme(foregroundColors));
    }

    public void setBackground(Color[] c) {
        backgroundColors = c;
        super.setBackground(ColorUtil.forCurrentTheme(backgroundColors));
    }

    @Override
    public void updateUI() {
        super.updateUI();
        super.setForeground(ColorUtil.forCurrentTheme(foregroundColors));
        super.setBackground(ColorUtil.forCurrentTheme(backgroundColors));
    }
}
