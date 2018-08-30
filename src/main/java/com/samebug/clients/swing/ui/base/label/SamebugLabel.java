/*
 * Copyright 2018 Samebug, Inc.
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
package com.samebug.clients.swing.ui.base.label;

import com.samebug.clients.swing.ui.modules.ColorService;
import com.samebug.clients.swing.ui.modules.FontService;

import javax.swing.*;
import java.awt.*;

public class SamebugLabel extends JLabel {
    private Color[] foregroundColors;
    private Color[] backgroundColors;

    public SamebugLabel() {
        this(null);
    }

    public SamebugLabel(String text) {
        this(text, FontService.regular(16));
    }

    public SamebugLabel(String text, Font font) {
        super(text);
        setForegroundColor(ColorService.Text);
        setFont(font);
    }

    public void setForegroundColor(Color[] c) {
        foregroundColors = c;
        updateColors();
    }

    public void setBackgroundColor(Color[] c) {
        backgroundColors = c;
        updateColors();
    }

    @Override
    public void updateUI() {
        setUI(new SamebugLabelUI());
        updateColors();
    }

    private void updateColors() {
        setForeground(ColorService.forCurrentTheme(foregroundColors));
        setBackground(ColorService.forCurrentTheme(backgroundColors));
    }
}
