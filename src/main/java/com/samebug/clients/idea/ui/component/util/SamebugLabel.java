package com.samebug.clients.idea.ui.component.util;

import com.samebug.clients.idea.ui.ColorUtil;

import javax.swing.*;
import java.awt.*;

public class SamebugLabel extends JLabel {
    public SamebugLabel(String text, String fontName, int fontSize) {
        super(text);
        setFont(new Font(fontName, Font.PLAIN, fontSize));
    }
    @Override
    public void updateUI() {
        super.updateUI();
        setForeground(ColorUtil.text());
    }
}
