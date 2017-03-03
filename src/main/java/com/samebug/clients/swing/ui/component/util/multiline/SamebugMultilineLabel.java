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
package com.samebug.clients.swing.ui.component.util.multiline;

import com.samebug.clients.swing.ui.ColorUtil;
import com.samebug.clients.swing.ui.FontRegistry;

import javax.swing.*;
import javax.swing.plaf.basic.BasicTextAreaUI;
import java.awt.*;

public class SamebugMultilineLabel extends JTextArea {
    private Color[] foregroundColors;
    private Color[] backgroundColors;

    {
        setEditable(false);
        setCursor(null);
        setFocusable(false);
        setWrapStyleWord(true);
        setLineWrap(true);

        setForeground(ColorUtil.Text);
        setFont(FontRegistry.regular(16));
        setOpaque(false);
    }

    public void setForeground(Color[] c) {
        foregroundColors = c;
        setForeground(ColorUtil.forCurrentTheme(foregroundColors));
    }

    public void setBackground(Color[] c) {
        backgroundColors = c;
        setBackground(ColorUtil.forCurrentTheme(backgroundColors));
    }

    @Override
    public void updateUI() {
        setUI(new BasicTextAreaUI());
        setForeground(ColorUtil.forCurrentTheme(foregroundColors));
        setBackground(ColorUtil.forCurrentTheme(backgroundColors));
    }
}
