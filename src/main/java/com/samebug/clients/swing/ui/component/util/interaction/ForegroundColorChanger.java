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
package com.samebug.clients.swing.ui.component.util.interaction;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;

public class ForegroundColorChanger extends BasicInteractionListener {
    protected final JComponent component;
    protected final Colors colors;

    public ForegroundColorChanger(Colors colors, JComponent component) {
        this.component = component;
        this.colors = colors;
    }

    @Override
    public void mousePressed(MouseEvent e) {
        pressed = true;
        component.setForeground(getColor());
        super.mousePressed(e);
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        pressed = false;
        component.setForeground(getColor());
        super.mouseReleased(e);
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        rollover = true;
        component.setForeground(getColor());
        super.mouseEntered(e);
    }

    @Override
    public void mouseExited(MouseEvent e) {
        rollover = false;
        component.setForeground(getColor());
        super.mouseExited(e);
    }

    public Color getColor() {
        if (pressed) return colors.pressed;
        else if (rollover) return colors.rollover;
        else return colors.normal;
    }

    public static ForegroundColorChanger installForegroundInteraction(@Nullable Colors interationColors, @NotNull JComponent component) {
        ForegroundColorChanger listener = new ForegroundColorChanger(interationColors, component);
        component.addMouseListener(listener);
        return listener;
    }

    @Nullable
    public static ForegroundColorChanger updateForegroundInteraction(
            @Nullable ForegroundColorChanger currentListener,
            @Nullable Colors interationColors,
            @NotNull JComponent component) {
        ForegroundColorChanger newListener = null;
        if (interationColors != null) {
            if (currentListener != null) currentListener.component.removeMouseListener(currentListener);
            newListener = new ForegroundColorChanger(interationColors, component);
            component.addMouseListener(newListener);
            component.setForeground(interationColors.normal);
        }
        return newListener;
    }
}
