package com.samebug.clients.idea.ui.component.util;

import com.samebug.clients.idea.ui.ColorUtil;

import javax.swing.*;
import java.awt.*;

public class SamebugLabel extends JLabel {
    public SamebugLabel(String text, String fontName, int fontSize) {
        super(text);
        setForeground(ColorUtil.text());
        setFont(new Font(fontName, Font.PLAIN, fontSize));
    }
}
