package com.samebug.clients.idea.ui.views.components;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Created by poroszd on 4/21/16.
 */
// TODO it mimics a JButton, but is actually a JPanel, and quite fragile.
//    It would be better to use Look&Feel correctly to achive the L&F we want.
public class SBButton extends JPanel {
    public final JButton button;

    public SBButton(final String label) {
        this.button = new Button(label);

        setLayout(new BorderLayout());
        add(button);
        setHighlighted(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    class Button extends JButton {
        public Button(final String label) {
            setText(label);
            setFocusable(false);
            setContentAreaFilled(false);
            setBorder(null);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        }
    }

    public void setHighlighted(boolean on) {
        if (on) {
            final Color highlightColor = getBackground();
            final Color textColor = getForeground();

            setOpaque(true);
            setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
            button.setForeground(textColor);
        } else {
            final Color highlightColor = getBackground();
            final Color textColor = getForeground();

            setOpaque(false);
            setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(highlightColor, 1),
                    BorderFactory.createEmptyBorder(4, 9, 4, 9)
            ));
            button.setForeground(highlightColor);
        }

    }

    // TODO this is especially nasty
    @Override
    public void addMouseListener(MouseListener l) {
        super.addMouseListener(l);
        button.addMouseListener(l);
    }
}
