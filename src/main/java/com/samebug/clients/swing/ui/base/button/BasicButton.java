package com.samebug.clients.swing.ui.base.button;

import com.samebug.clients.swing.ui.base.interaction.InteractionColors;
import com.samebug.clients.swing.ui.base.interaction.ForegroundColorChanger;
import com.samebug.clients.swing.ui.modules.ColorService;
import com.samebug.clients.swing.ui.modules.DrawService;

import javax.swing.*;
import java.awt.*;

/**
 * Samebug style button
 *
 * By not extending the swing AbstractButton, we have lost couple of functionality, e.g. tab selection, mnemonics, accessibility.
 * However, atm we don't need those, and the features that we actually need are much easier to implement on a clean JComponent.
 *
 * If it happens that we need those features from AbstractButton, still don't try to extend it, it is simply not suitable for
 * this level of reusability. Simply lift the necessary code.
 *
 * The features this button provides:
 *  - samebug-style interaction (separate colors for normal/rollover/pressed states)
 *  - intellij-style themes (separate colors for light/dark theme)
 *  - arbitrary component as content
 *  - samebug-style filled/not filled state
 */
public abstract class BasicButton extends JComponent {
    protected ForegroundColorChanger interactionListener;
    protected Color[] backgroundColor;
    protected boolean filled;

    public BasicButton(boolean filled) {
        this.filled = filled;

        setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        setOpaque(false);

        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    public void setInteractionColors(InteractionColors c) {
        interactionListener = ForegroundColorChanger.installForegroundInteraction(c, this);
        updateColors();
    }

    public void setBackgroundColor(Color[] c) {
        backgroundColor = c;
        updateColors();
    }

    public void setFilled(boolean filled) {
        this.filled = filled;
        if (!filled) setChildrenForeground(getForeground());
        else setChildrenForeground(getBackground());
        repaint();
    }

    public boolean isFilled() {
        return filled;
    }

    @Override
    public void setFont(Font f) {
        super.setFont(f);
        setChildrenFont(f);
    }

    @Override
    public void setForeground(Color c) {
        super.setForeground(c);
        if (!filled) setChildrenForeground(c);
    }

    @Override
    public void setBackground(Color c) {
        super.setBackground(c);
        if (filled) setChildrenForeground(c);
    }

    @Override
    public void paint(Graphics g) {
        Graphics2D g2 = DrawService.init(g);
        paintBorder(g2);
        paintContent(g2);
    }

    private void paintBorder(Graphics2D g2) {
        // draw the rounded border
        g2.setColor(getForeground());
        if (filled) g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, DrawService.RoundingDiameter, DrawService.RoundingDiameter);
        else g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, DrawService.RoundingDiameter, DrawService.RoundingDiameter);
    }

    private void paintContent(Graphics2D g2) {
        // let the children paint themselves
        super.paint(g2);
    }

    @Override
    public void updateUI() {
        super.updateUI();
        updateColors();
    }

    private void updateColors() {
        if (interactionListener != null) setForeground(interactionListener.getColor());
        setBackground(ColorService.forCurrentTheme(backgroundColor));
    }

    protected abstract void setChildrenForeground(Color foreground);
    protected abstract void setChildrenFont(Font font);
}
