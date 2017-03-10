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
package com.samebug.clients.swing.ui.base.button;

import com.samebug.clients.swing.ui.modules.ColorService;
import com.samebug.clients.swing.ui.modules.FontService;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;

/**
 * Ordinary button with a single text label, and sensible defaults.
 */
public class SamebugButton extends BasicButton {
    protected final JLabel label;

    public SamebugButton() {
        this(null);
    }

    public SamebugButton(String text) {
        this(text, false);
    }

    public SamebugButton(String text, boolean filled) {
        super(filled);
        label = new JLabel(text);

        setFont(FontService.demi(14));
        setLayout(new MigLayout("", "12:push[]12:push", "11[]12"));
        add(label, "align center");

        setInteractionColors(ColorService.LinkInteraction);
        setBackgroundColor(ColorService.Background);
    }

    public void setText(String t) {
        label.setText(t);
    }

    @Override
    protected void setChildrenForeground(Color foreground) {
        label.setForeground(foreground);
    }

    @Override
    protected void setChildrenFont(Font font) {
        label.setFont(font);
    }
}
