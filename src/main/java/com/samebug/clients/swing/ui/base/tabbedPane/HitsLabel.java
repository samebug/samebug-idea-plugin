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
package com.samebug.clients.swing.ui.base.tabbedPane;

import com.samebug.clients.swing.ui.base.label.SamebugLabel;
import com.samebug.clients.swing.ui.modules.DrawService;
import com.samebug.clients.swing.ui.modules.FontService;

import java.awt.*;

public class HitsLabel extends SamebugLabel {
    public static final int SMALL = 20;
    public static final int LARGE = 22;

    private final int size;
    private final int xCorrection;
    private final int yCorrection;
    private final Font font;

    public HitsLabel(int size) {
        this.size = size;
        // IMPROVE it would be nice to generalize
        int fontSize;
        if (size == SMALL) {
            fontSize = 10;
            xCorrection = 8;
            yCorrection = 13;
        } else if (size == LARGE) {
            fontSize = 14;
            xCorrection = 8;
            yCorrection = 16;
        } else {
            throw new IllegalArgumentException("The calculations for this parameter are not generalized. Time to complement");
        }
        font = FontService.demi(fontSize);
    }

    @Override
    public Dimension getPreferredSize() {
        // Override to guarantee size
        String hits = getText();

        if (hits.length() == 1) {
            // the background is a circle
            return new Dimension(size, size);
        } else {
            // the background is a rounded rect
            FontMetrics fm = getFontMetrics(font);
            int width = fm.stringWidth(hits);
            return new Dimension(xCorrection + width + xCorrection, size);
        }
    }

    @Override
    public void paint(Graphics g) {
        Graphics2D g2 = DrawService.init(g);

        // NOTE different behaviour for one and for more digits
        // For one digit, we have a disk as background, and show the number in the center
        // For more digit, we have a rounded rectangle, and push the number to the right (to be in the center of the rectangle
        String hits = getText();

        g2.setColor(getForeground());
        if (hits.length() == 1) {
            g.fillOval(1, 1, getWidth() - 2, getHeight() - 2);
        } else {
            g.fillRoundRect(1, 1, getWidth() - 2, getHeight() - 2, size, size);
        }

        g2.setColor(getBackground());
        g2.setFont(font);

        FontMetrics fm = getFontMetrics(font);
        int labelWidth = getWidth();
        int textWidth = fm.stringWidth(hits);
        // round up on the x axis because it looks nicer
        g2.drawString(getText(), (labelWidth - textWidth + 1) / 2, yCorrection);
    }
}
