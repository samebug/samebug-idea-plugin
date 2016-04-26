/**
 * Copyright 2016 Samebug, Inc.
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
package com.samebug.clients.idea.ui.component;

import com.samebug.clients.idea.ui.listeners.RedispatchingMouseAdapter;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;

// TODO it mimics a JButton, but is actually a JPanel.
// Simple JButton styling will be broken by com.intellij.ide.ui.laf.darcula.ui.DarculaButtonUI
// when the button has a border.
// The other (possibly better) option would be to override paint().
public class SBButton extends JPanel {
    public final JButton button;

    public SBButton(@NotNull final String label) {
        this.button = new Button(label);

        setLayout(new BorderLayout());
        add(button);
        setHighlighted(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    class Button extends JButton {
        public Button(final String label) {
            setText(label);
            setFocusable(false);
            setContentAreaFilled(false);
            setBorder(null);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            addMouseListener(RedispatchingMouseAdapter.INSTANCE);
        }

        @Override
        public String getToolTipText() {
            return SBButton.this.getToolTipText();
        }
    }

    public void setHighlighted(boolean on) {
        if (on) {
            final Color highlightColor = getBackground();
            final Color textColor = getForeground();

            setOpaque(true);
            setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
            button.setForeground(textColor);
        } else {
            final Color highlightColor = getBackground();
            final Color textColor = getForeground();

            setOpaque(false);
            setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(highlightColor, 1),
                    BorderFactory.createEmptyBorder(4, 9, 4, 9)
            ));
            button.setForeground(highlightColor);
        }
    }
}
