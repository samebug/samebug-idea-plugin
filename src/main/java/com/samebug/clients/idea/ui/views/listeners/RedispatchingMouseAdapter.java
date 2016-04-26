package com.samebug.clients.idea.ui.views.listeners;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

// from http://stackoverflow.com/a/32204965/1209166
public class RedispatchingMouseAdapter implements MouseListener, MouseWheelListener, MouseMotionListener {
    public static final RedispatchingMouseAdapter INSTANCE = new RedispatchingMouseAdapter();

    public void mouseClicked(MouseEvent e) {
        redispatchToParent(e);
    }

    public void mousePressed(MouseEvent e) {
        redispatchToParent(e);
    }

    public void mouseReleased(MouseEvent e) {
        redispatchToParent(e);
    }

    public void mouseEntered(MouseEvent e) {
        redispatchToParent(e);
    }

    public void mouseExited(MouseEvent e) {
        redispatchToParent(e);
    }

    public void mouseWheelMoved(MouseWheelEvent e){
        redispatchToParent(e);
    }

    public void mouseDragged(MouseEvent e){
        redispatchToParent(e);
    }

    public void mouseMoved(MouseEvent e) {
        redispatchToParent(e);
    }

    private void redispatchToParent(MouseEvent e){
        Component source = (Component) e.getSource();
        MouseEvent parentEvent = SwingUtilities.convertMouseEvent(source, e, source.getParent());
        source.getParent().dispatchEvent(parentEvent);
    }
}
