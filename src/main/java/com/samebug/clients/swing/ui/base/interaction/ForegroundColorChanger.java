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
package com.samebug.clients.swing.ui.base.interaction;

import com.samebug.clients.swing.ui.modules.ColorService;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;

public class ForegroundColorChanger extends BasicInteractionListener {
    protected final JComponent component;
    protected final InteractionColors interactionColors;

    public ForegroundColorChanger(InteractionColors interactionColors, JComponent component) {
        this.component = component;
        this.interactionColors = interactionColors;
    }

    @Override
    public void mousePressed(MouseEvent e) {
        super.mousePressed(e);
        component.setForeground(getColor());
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        super.mouseReleased(e);
        component.setForeground(getColor());
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        super.mouseEntered(e);
        component.setForeground(getColor());
    }

    @Override
    public void mouseExited(MouseEvent e) {
        super.mouseExited(e);
        component.setForeground(getColor());
    }

    public Color getColor() {
        Color[] colors;
        if (pressed) colors = interactionColors.pressed;
        else if (rollover) colors = interactionColors.rollover;
        else colors = interactionColors.normal;
        return ColorService.forCurrentTheme(colors);
    }

    public static ForegroundColorChanger installForegroundInteraction(@Nullable InteractionColors interationInteractionColors, @NotNull JComponent component) {
        ForegroundColorChanger listener = new ForegroundColorChanger(interationInteractionColors, component);
        component.addMouseListener(listener);
        return listener;
    }
}
