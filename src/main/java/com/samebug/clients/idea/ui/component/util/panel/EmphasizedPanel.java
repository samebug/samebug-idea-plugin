package com.samebug.clients.idea.ui.component.util.panel;

import com.samebug.clients.idea.ui.ColorUtil;
import com.samebug.clients.idea.ui.DrawUtil;

import java.awt.*;

public class EmphasizedPanel extends TransparentPanel {
    {
        setForeground(ColorUtil.Separator);
    }

    @Override
    public void paintBorder(Graphics g) {
        Graphics2D g2 = DrawUtil.init(g);

        g2.setColor(getForeground());
        g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, DrawUtil.RoundingDiameter, DrawUtil.RoundingDiameter);
    }
}
