package com.samebug.clients.idea.ui.component.util.button;

import com.samebug.clients.idea.ui.ColorUtil;
import com.samebug.clients.idea.ui.DrawUtil;
import com.samebug.clients.idea.ui.FontRegistry;
import com.samebug.clients.idea.ui.component.util.interaction.Colors;
import com.samebug.clients.idea.ui.component.util.interaction.InteractiveComponent;

import javax.swing.*;
import javax.swing.plaf.basic.BasicButtonUI;
import java.awt.*;

public class SamebugButton extends JButton {
    private InteractiveComponent interactiveComponent;
    private Colors[] foregroundColors;
    private Color[] backgroundColors;

    {
        setBorder(BorderFactory.createEmptyBorder(12, 12, 11, 12));
        setContentAreaFilled(false);
        setOpaque(false);
        setFont(new Font(FontRegistry.AvenirDemi, Font.PLAIN, 14));

        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        setForeground(ColorUtil.LinkInteraction);
        setBackground(ColorUtil.Background);

        updateUI();
    }

    public void setForeground(Colors[] c) {
        foregroundColors = c;
        super.setForeground(ColorUtil.forCurrentTheme(foregroundColors).normal);
    }

    public void setBackground(Color[] c) {
        backgroundColors = c;
        super.setBackground(ColorUtil.forCurrentTheme(backgroundColors));
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

    @Override
    public void updateUI() {
        setUI(new BasicButtonUI());
        if (foregroundColors != null) {
            if (interactiveComponent != null) interactiveComponent.uninstall();
            interactiveComponent = new InteractiveComponent(this, ColorUtil.forCurrentTheme(foregroundColors));
            super.setForeground(ColorUtil.forCurrentTheme(foregroundColors).normal);
        }
    }
}
