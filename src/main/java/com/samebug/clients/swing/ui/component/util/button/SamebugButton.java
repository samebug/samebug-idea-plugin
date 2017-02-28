package com.samebug.clients.swing.ui.component.util.button;

import com.samebug.clients.swing.ui.ColorUtil;
import com.samebug.clients.swing.ui.DrawUtil;
import com.samebug.clients.swing.ui.FontRegistry;
import com.samebug.clients.swing.ui.component.util.interaction.Colors;
import com.samebug.clients.swing.ui.component.util.interaction.ForegroundColorChanger;

import javax.swing.*;
import java.awt.*;

public class SamebugButton extends JButton {
    protected ForegroundColorChanger interactionListener;
    protected Colors[] foregroundColors;
    protected Color[] backgroundColors;
    protected boolean filled;

    public SamebugButton() {
        this(null);
    }

    public SamebugButton(String text) {
        this(text, false);
    }

    public SamebugButton(String text, boolean filled) {
        super(text);
        this.filled = filled;

        setBorder(BorderFactory.createEmptyBorder(12, 12, 11, 12));
        setContentAreaFilled(false);
        setOpaque(false);
        setFont(FontRegistry.demi( 14));

        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        setForeground(ColorUtil.LinkInteraction);
        setBackground(ColorUtil.Background);

        updateUI();
    }

    public void setForeground(Colors[] c) {
        foregroundColors = c;
        setForeground(ColorUtil.forCurrentTheme(foregroundColors).normal);
    }

    public void setBackground(Color[] c) {
        backgroundColors = c;
        setBackground(ColorUtil.forCurrentTheme(backgroundColors));
    }

    public void setFilled(boolean filled) {
        this.filled = filled;
        repaint();
    }

    public boolean isFilled() {
        return filled;
    }

    @Override
    public void paint(Graphics g) {
        Graphics2D g2 = DrawUtil.init(g);
        paintBorder(g2);
        paintContent(g2);
    }

    protected void paintBorder(Graphics2D g2) {
        // draw the rounded border
        g2.setColor(getForeground());
        if (filled) g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, DrawUtil.RoundingDiameter, DrawUtil.RoundingDiameter);
        else g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, DrawUtil.RoundingDiameter, DrawUtil.RoundingDiameter);
    }

    protected void paintContent(Graphics2D g2) {
        // let the SamebugButtonUI paint the text
        super.paint(g2);
    }

    @Override
    public void updateUI() {
        setUI(new SamebugButtonUI());
        interactionListener = ForegroundColorChanger.updateForegroundInteraction(interactionListener, ColorUtil.forCurrentTheme(foregroundColors), this);
    }
}
