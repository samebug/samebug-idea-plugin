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

import com.samebug.clients.swing.ui.base.interaction.ForegroundColorChanger;
import com.samebug.clients.swing.ui.base.interaction.InteractionColors;
import com.samebug.clients.swing.ui.modules.ColorService;
import com.samebug.clients.swing.ui.modules.FontService;

import javax.swing.*;
import javax.swing.plaf.basic.BasicTextAreaUI;
import javax.swing.text.DefaultCaret;
import java.awt.*;

public class LinkMultilineLabel extends JTextArea {
    private ForegroundColorChanger interactionListener;

    {
        setEditable(false);
        ((DefaultCaret) getCaret()).setUpdatePolicy(DefaultCaret.NEVER_UPDATE);
        setFocusable(false);
        setWrapStyleWord(true);
        setLineWrap(true);

        setInteractionColors(ColorService.LinkInteraction);
        setFont(FontService.regular(16));
        setOpaque(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        updateColors();
    }

    public void setInteractionColors(InteractionColors c) {
        if (interactionListener != null) interactionListener.uninstall();
        interactionListener = ForegroundColorChanger.installForegroundInteraction(c, this);
        updateColors();
    }

    @Override
    public void updateUI() {
        setUI(new BasicTextAreaUI());
        updateColors();
    }

    private void updateColors() {
        if (interactionListener != null) setForeground(interactionListener.getColor());
    }
}