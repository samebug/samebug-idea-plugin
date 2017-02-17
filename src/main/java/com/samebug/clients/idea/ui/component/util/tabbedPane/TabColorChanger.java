package com.samebug.clients.idea.ui.component.util.tabbedPane;

import com.samebug.clients.idea.ui.component.util.interaction.Colors;
import com.samebug.clients.idea.ui.component.util.interaction.ForegroundColorChanger;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;

final class TabColorChanger extends ForegroundColorChanger {

    TabColorChanger(Colors colors, JComponent component) {
        super(colors, component);
    }

    @Override
    public void mousePressed(MouseEvent e) {
        super.mousePressed(e);
        dispatchToPane(e);
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        super.mouseReleased(e);
        dispatchToPane(e);
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        super.mouseEntered(e);
        dispatchToPane(e);
    }

    @Override
    public void mouseExited(MouseEvent e) {
        super.mouseExited(e);
        dispatchToPane(e);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        super.mouseClicked(e);
        dispatchToPane(e);
    }

    private void dispatchToPane(MouseEvent e) {
        JTabbedPane pane = getParentPane();
        if (pane != null) {
            Component source = (Component) e.getSource();
            MouseEvent parentEvent = SwingUtilities.convertMouseEvent(source, e, pane);
            pane.dispatchEvent(parentEvent);
        }
    }

    @Nullable
    private JTabbedPane getParentPane() {
        Component parent = component.getParent();
        while (parent != null && !(parent instanceof JTabbedPane)) parent = parent.getParent();
        return (JTabbedPane) parent;
    }

    static TabColorChanger createTabColorChanger(JComponent interactiveComponent, Colors colors) {
        return new TabColorChanger(colors, interactiveComponent);
    }

}
