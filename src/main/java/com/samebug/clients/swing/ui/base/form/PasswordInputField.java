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
package com.samebug.clients.swing.ui.base.form;

import com.samebug.clients.swing.ui.modules.ColorService;
import com.samebug.clients.swing.ui.modules.DrawService;
import com.samebug.clients.swing.ui.modules.FontService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

// TODO copy of InputField, extract
public class PasswordInputField extends JPasswordField {
    public static final String ERROR_PROPERTY = "samebug.error";

    protected FormColors myColors;
    protected boolean hasError = false;

    {
        myColors = ColorService.NormalForm;
        setFont(FontService.regular(16));
        // Mac uses a different default echo character, which is not present in out font set.
        setEchoChar('\u2022');
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                setError(false);
            }

            @Override
            public void focusLost(FocusEvent e) {
                repaint();
            }
        });

    }

    public void setError(boolean hasError) {
        firePropertyChange(ERROR_PROPERTY, this.hasError, hasError);
        this.hasError = hasError;
        repaint();
    }

    @Override
    public void paintBorder(Graphics g) {
        Graphics2D g2 = DrawService.init(g);

        Color borderColor;
        if (hasError) borderColor = ColorService.forCurrentTheme(myColors.errorBorder);
        else if (hasFocus()) borderColor = ColorService.forCurrentTheme(myColors.focusBorder);
        else borderColor = ColorService.forCurrentTheme(myColors.normalBorder);
        g2.setColor(borderColor);
        g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, DrawService.RoundingDiameter, DrawService.RoundingDiameter);
    }
}
