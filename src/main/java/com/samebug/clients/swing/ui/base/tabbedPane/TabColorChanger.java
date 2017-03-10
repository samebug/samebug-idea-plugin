/*
 * Copyright 2017 Samebug, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 *    http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.samebug.clients.swing.ui.base.tabbedPane;

import com.samebug.clients.swing.ui.base.interaction.Colors;
import com.samebug.clients.swing.ui.base.interaction.ForegroundColorChanger;
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
