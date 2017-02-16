package com.samebug.clients.idea.ui.component.util.scrollPane;

import com.samebug.clients.idea.ui.ColorUtil;

import javax.swing.*;
import java.awt.*;

public class SamebugScrollPane extends JScrollPane {
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
        setBackground(ColorUtil.background());
        setBorder(null);
    }
}
