package com.samebug.clients.idea.ui.component.util.interaction;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;


public abstract class BasicInteractionListener extends MouseAdapter {
    protected boolean rollover = false;
    protected boolean pressed = false;

    @Override
    public void mousePressed(MouseEvent e) {
        pressed = true;
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        pressed = false;
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        rollover = true;
    }

    @Override
    public void mouseExited(MouseEvent e) {
        rollover = false;
    }
}
