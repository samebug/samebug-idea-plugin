package com.samebug.clients.swing.ui.base.panel;

import com.samebug.clients.swing.ui.modules.DrawService;

import java.awt.*;

public class RoundedBackgroundPanel extends TransparentPanel {
    @Override
    public void paintBorder(Graphics g) {
        Graphics2D g2 = DrawService.init(g);
        g2.setColor(getBackground());
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), DrawService.RoundingDiameter, DrawService.RoundingDiameter);
    }
}
