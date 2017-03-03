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
package com.samebug.clients.swing.ui.component.util.label;

import com.samebug.clients.swing.ui.ColorUtil;
import com.samebug.clients.swing.ui.FontRegistry;
import com.samebug.clients.swing.ui.component.util.interaction.Colors;
import com.samebug.clients.swing.ui.component.util.interaction.ForegroundColorChanger;

import javax.swing.*;
import java.awt.*;

public class LinkLabel extends JLabel {
    private Colors[] foregroundColors;
    private ForegroundColorChanger interactionListener;

    public LinkLabel() {
        this(null);
    }

    public LinkLabel(String text) {
        this(text, FontRegistry.regular(16));
    }

    public LinkLabel(String text, Font font) {
        super(text);
        setForeground(ColorUtil.LinkInteraction);
        setFont(font);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        updateUI();
    }

    public void setForeground(Colors[] c) {
        foregroundColors = c;
        super.setForeground(ColorUtil.forCurrentTheme(foregroundColors).normal);
    }

    @Override
    public void updateUI() {
        setUI(new SamebugLabelUI());
        interactionListener = ForegroundColorChanger.updateForegroundInteraction(interactionListener, ColorUtil.forCurrentTheme(foregroundColors), this);
    }
}
