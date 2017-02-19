/**
 * Copyright 2017 Samebug, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.samebug.clients.idea.ui.controller.history;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

// from http://stackoverflow.com/a/32204965/1209166
final public class RedispatchingMouseAdapter implements MouseListener, MouseWheelListener, MouseMotionListener {
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

    public void mouseWheelMoved(MouseWheelEvent e) {
        redispatchToParent(e);
    }

    public void mouseDragged(MouseEvent e) {
        redispatchToParent(e);
    }

    public void mouseMoved(MouseEvent e) {
        redispatchToParent(e);
    }

    private void redispatchToParent(MouseEvent e) {
        Component source = (Component) e.getSource();
        MouseEvent parentEvent = SwingUtilities.convertMouseEvent(source, e, source.getParent());
        source.getParent().dispatchEvent(parentEvent);
    }
}
