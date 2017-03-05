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
package com.samebug.clients.swing.ui.component.profile;

import com.samebug.clients.swing.ui.component.util.interaction.Colors;
import com.samebug.clients.swing.ui.component.util.interaction.ForegroundColorChanger;
import com.samebug.clients.swing.ui.global.ColorService;
import com.samebug.clients.swing.ui.global.MessageService;

import java.awt.*;

public final class MessageLabel extends NumberLabel {
    private Colors[] foregroundColors;
    private ForegroundColorChanger interactionListener;

    public MessageLabel(int nMessages) {
        super(nMessages, MessageService.message("samebug.component.profile.messages.label"));

        setForeground(ColorService.SecondaryLinkInteraction);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        updateUI();
    }

    public void setForeground(Colors[] c) {
        foregroundColors = c;
        setForeground(ColorService.forCurrentTheme(foregroundColors).normal);
    }

    @Override
    public void updateUI() {
        super.updateUI();
        interactionListener = ForegroundColorChanger.updateForegroundInteraction(interactionListener, ColorService.forCurrentTheme(foregroundColors), this);
    }
}
