package com.samebug.clients.idea.ui.component.util.label;

import com.samebug.clients.idea.ui.ColorUtil;
import com.samebug.clients.idea.ui.FontRegistry;

import javax.swing.*;
import java.awt.*;

public class SamebugLabel extends JLabel {
    private Color[] foregroundColors;
    private Color[] backgroundColors;

    public SamebugLabel() {
        this(null);
    }

    public SamebugLabel(String text) {
        this(text, FontRegistry.AvenirRegular, 16);
    }

    public SamebugLabel(String text, String fontName, int fontSize) {
        super(text);
        setForeground(ColorUtil.Text);
        setFont(new Font(fontName, Font.PLAIN, fontSize));
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
