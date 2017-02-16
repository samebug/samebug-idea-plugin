package com.samebug.clients.idea.ui.component.util.label;

import com.samebug.clients.idea.ui.ColorUtil;
import com.samebug.clients.idea.ui.FontRegistry;

import javax.swing.*;
import java.awt.*;

public class Label extends JLabel {
    private Color[] foregroundColors;
    private Color[] backgroundColors;

    public Label() {
        this(null);
    }

    public Label(String text) {
        this(text, FontRegistry.AvenirRegular, 16);
    }

    public Label(String text, String fontName, int fontSize) {
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
