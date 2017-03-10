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

import com.samebug.clients.swing.ui.base.interaction.Colors;
import com.samebug.clients.swing.ui.base.interaction.ForegroundColorChanger;
import com.samebug.clients.swing.ui.modules.ColorService;
import com.samebug.clients.swing.ui.modules.DrawService;
import com.samebug.clients.swing.ui.modules.FontService;

import javax.swing.*;
import java.awt.*;

public class SamebugButton extends JButton {
    protected ForegroundColorChanger interactionListener;
    protected Colors[] foregroundColors;
    protected Color[] backgroundColors;
    protected boolean filled;

    public SamebugButton() {
        this(null);
    }

    public SamebugButton(String text) {
        this(text, false);
    }

    public SamebugButton(String text, boolean filled) {
        super(text);
        this.filled = filled;

        setBorder(BorderFactory.createEmptyBorder(12, 12, 11, 12));
        setContentAreaFilled(false);
        setOpaque(false);
        setFont(FontService.demi(14));

        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        setInteractionColors(ColorService.LinkInteraction);
        setBackgroundColors(ColorService.Background);
    }

    public void setInteractionColors(Colors[] c) {
        foregroundColors = c;
        setForeground(ColorService.forCurrentTheme(foregroundColors).normal);
        interactionListener = ForegroundColorChanger.updateForegroundInteraction(interactionListener, ColorService.forCurrentTheme(foregroundColors), this);
    }

    public void setBackgroundColors(Color[] c) {
        backgroundColors = c;
        setBackground(ColorService.forCurrentTheme(backgroundColors));
    }

    public void setFilled(boolean filled) {
        this.filled = filled;
        repaint();
    }

    public boolean isFilled() {
        return filled;
    }

    @Override
    public void paint(Graphics g) {
        Graphics2D g2 = DrawService.init(g);
        paintBorder(g2);
        paintContent(g2);
    }

    protected void paintBorder(Graphics2D g2) {
        // draw the rounded border
        g2.setColor(getForeground());
        if (filled) g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, DrawService.RoundingDiameter, DrawService.RoundingDiameter);
        else g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, DrawService.RoundingDiameter, DrawService.RoundingDiameter);
    }

    protected void paintContent(Graphics2D g2) {
        // let the SamebugButtonUI paint the text
        super.paint(g2);
    }

    @Override
    public void updateUI() {
        setUI(new SamebugButtonUI());
    }
}
