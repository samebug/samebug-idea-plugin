package com.samebug.clients.idea.ui.component.util;

import com.samebug.clients.idea.ui.ColorUtil;
import com.samebug.clients.idea.ui.DrawUtil;
import com.samebug.clients.idea.ui.FontRegistry;

import javax.swing.*;
import javax.swing.plaf.basic.BasicButtonUI;
import java.awt.*;

public class SamebugButton extends JButton {
    {
        setBorder(BorderFactory.createEmptyBorder(12, 12, 11, 12));
        setContentAreaFilled(false);
        setUI(new BasicButtonUI()); // override jetbrains custom UI
        setOpaque(false);
        setForeground(ColorUtil.samebug());
        setFont(new Font(FontRegistry.AvenirDemi, Font.PLAIN, 14));
    }

    @Override
    public void paint(Graphics g) {
        Graphics2D g2 = DrawUtil.init(g);

        // draw the rounded border
        g2.setColor(getForeground());
        g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 5, 5);
        // the button content is drawed by the default implementation
        super.paint(g);
    }
}
