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
import org.jdesktop.swingx.prompt.PromptSupport;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.plaf.basic.BasicTextFieldUI;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

// TODO extract common code with other form fields, like border painting
public class InputField extends JTextField {
    public static final String ERROR_PROPERTY = "samebug.error";

    protected FormColors myColors;
    protected boolean hasError = false;

    public InputField(@Nullable String prompt) {
        myColors = ColorService.NormalForm;
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        updateColors();
        PromptSupport.setPrompt(prompt, this);
        PromptSupport.setFocusBehavior(PromptSupport.FocusBehavior.SHOW_PROMPT, this);
        setFont(FontService.regular(16));

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

    public void setColors(@NotNull FormColors colors) {
        myColors = colors;
        updateColors();
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

    public void updateColors() {
        Color currentColor = ColorService.forCurrentTheme(myColors.text);
        setCaretColor(currentColor);
        super.setForeground(currentColor);
        PromptSupport.setForeground(ColorService.forCurrentTheme(myColors.placeholder), this);
    }

    @Override
    public void updateUI() {
        setUI(new BasicTextFieldUI());
        if (myColors != null) updateColors();
    }

}
