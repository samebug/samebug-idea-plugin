package com.samebug.clients.swing.ui;

import java.awt.*;

public final class DrawUtil {
    public static final int RoundingDiameter = 4;

    public static Graphics2D init(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON
        );
        return g2;
    }
}
