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
package com.samebug.clients.swing.ui.base.multiline;

import com.samebug.clients.swing.ui.modules.ColorService;
import com.samebug.clients.swing.ui.modules.FontService;

import javax.swing.*;
import javax.swing.plaf.basic.BasicTextPaneUI;
import javax.swing.text.DefaultCaret;
import java.awt.*;

public class StyledMultilineLabel extends JTextPane {
    private Color[] foregroundColors;
    private Color[] backgroundColors;

    {
        setEditable(false);
        ((DefaultCaret) getCaret()).setUpdatePolicy(DefaultCaret.NEVER_UPDATE);
        setCursor(null);
        setFocusable(false);
        setFont(FontService.regular(16));
        setForeground(ColorService.Text);
        setOpaque(false);
    }

    public void setForeground(Color[] c) {
        foregroundColors = c;
        updateColors();
    }

    public void setBackground(Color[] c) {
        backgroundColors = c;
        updateColors();
    }

    @Override
    public void updateUI() {
        setUI(new BasicTextPaneUI());
        updateColors();
    }

    private void updateColors() {
        setForeground(ColorService.forCurrentTheme(foregroundColors));
        setBackground(ColorService.forCurrentTheme(backgroundColors));
    }
}
