package com.samebug.clients.idea.ui.component.util;

import com.samebug.clients.idea.ui.ColorUtil;
import com.samebug.clients.idea.ui.FontRegistry;

import javax.swing.*;
import java.awt.*;

public class SamebugMultiLineLabel extends JTextArea {
    public SamebugMultiLineLabel() {
        super();
        setEditable(false);
        setCursor(null);
        setFocusable(false);
        setWrapStyleWord(true);
        setLineWrap(true);

        setFont(new Font(FontRegistry.AvenirRegular, Font.PLAIN, 16));
        setOpaque(false);
    }

    @Override
    public void updateUI() {
        super.updateUI();
        setForeground(ColorUtil.text());
    }
}
