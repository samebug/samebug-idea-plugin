package com.samebug.clients.idea.ui.components;

import com.samebug.clients.idea.ui.ColorUtil;

import javax.swing.*;
import java.awt.*;

/**
 * Created by poroszd on 4/1/16.
 */
public class SourceIcon extends JPanel {
    final Image iconImage;
    static final int width = 32;
    static final int height = 32;

    public SourceIcon(Image image) {
        iconImage = image;
        setPreferredSize(new Dimension(width, height));
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        Color iconBackgroundColor = ColorUtil.sourceIconBackground();
        if (iconBackgroundColor != null) {
            g2.setColor(iconBackgroundColor);
            g2.fillOval(0, 0, width, height);
        }
        g2.drawImage(iconImage, 2, 0, null, null);
    }
}
