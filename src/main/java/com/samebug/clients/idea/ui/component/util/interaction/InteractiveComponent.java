package com.samebug.clients.idea.ui.component.util.interaction;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;


// TODO if necessary, separate the state handling (rollover and pressed) and the action (set component color)
public final class InteractiveComponent {
    protected boolean rollover = false;
    protected boolean pressed = false;
    protected final JComponent component;
    protected final Colors colors;
    protected final InteractionListener listener;

    public InteractiveComponent(JComponent component, Colors colors) {
        this.component = component;
        this.colors = colors;
        listener = new InteractionListener();
        component.addMouseListener(listener);
    }

    public void uninstall() {
        component.removeMouseListener(listener);
    }

    protected void onEnter() {
        component.setForeground(getColor());
    }

    protected void onExit() {
        component.setForeground(getColor());
    }

    protected void onPress() {
        component.setForeground(getColor());
    }

    protected void onRelease() {
        component.setForeground(getColor());
    }

    protected Color getColor() {
        if (pressed) return colors.pressed;
        else if (rollover) return colors.rollover;
        else return colors.normal;
    }

    private final class InteractionListener extends MouseAdapter {
        @Override
        public void mousePressed(MouseEvent e) {
            pressed = true;
            onPress();
            super.mousePressed(e);
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            pressed = false;
            onRelease();
            super.mouseReleased(e);
        }

        @Override
        public void mouseEntered(MouseEvent e) {
            rollover = true;
            onEnter();
            super.mouseEntered(e);
        }

        @Override
        public void mouseExited(MouseEvent e) {
            rollover = false;
            onExit();
            super.mouseExited(e);
        }
    }
}
