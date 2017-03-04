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
package com.samebug.clients.swing.ui.component.util.tabbedPane;

import com.samebug.clients.swing.ui.global.ColorService;
import com.samebug.clients.swing.ui.global.DrawService;
import com.samebug.clients.swing.ui.global.FontService;
import com.samebug.clients.swing.ui.component.util.interaction.Colors;
import com.samebug.clients.swing.ui.component.util.label.SamebugLabel;

import javax.swing.*;
import javax.swing.plaf.basic.BasicPanelUI;
import java.awt.*;

public abstract class SamebugTabHeader extends JPanel {
    protected final SamebugLabel tabLabel;
    protected final HitsLabel hitsLabel;
    protected boolean selected;
    protected TabColorChanger interactionListener;
    private Colors[] clickableColors;
    private Color[] selectedColor;
    private Color[] selectedHitColor;

    public SamebugTabHeader(String tabName, int hits) {
        tabLabel = new SamebugLabel(tabName, FontService.demi(16));
        hitsLabel = new HitsLabel();
        hitsLabel.setText(Integer.toString(hits));
        clickableColors = ColorService.LinkInteraction;
        selectedColor = ColorService.Text;
        selectedHitColor = ColorService.SelectedTab;

        setOpaque(false);
        // NOTE the layout is specified in the derived classes, don't forget to introduce changes to both when necessary

        add(tabLabel, "cell 0 0");
        add(hitsLabel, "cell 1 0");
        updateUI();
    }

    public void setSelected(boolean selected) {
        boolean wasSelected = this.selected;
        this.selected = selected;

        if (!wasSelected && selected && interactionListener != null) {
            this.removeMouseListener(interactionListener);
            interactionListener = null;
        } else if (wasSelected && !selected) {
            interactionListener = TabColorChanger.createTabColorChanger(this, ColorService.forCurrentTheme(clickableColors));
            addMouseListener(interactionListener);
        }
        updateColors();
    }

    /**
     * Update foreground and background color of this component based on the current theme
     */
    private void updateColors() {
        Color foreground;
        if (selected) foreground = ColorService.forCurrentTheme(selectedColor);
        else foreground = ColorService.forCurrentTheme(clickableColors).normal;
        setForeground(foreground);
        setBackground(ColorService.forCurrentTheme(ColorService.Background));

        // hit label in selected state has a visually corrected color
        if (hitsLabel != null && selected) hitsLabel.setForeground(ColorService.forCurrentTheme(selectedHitColor));
    }

    /**
     * Update the current foreground color for this and child components.
     * NOTE: this is mostly for the interaction listener, to handle color change from rollover and pressed events
     */
    @Override
    public void setForeground(Color color) {
        super.setForeground(color);
        for (Component c : getComponents()) c.setForeground(color);
    }

    @Override
    public void setBackground(Color color) {
        super.setBackground(color);
        for (Component c : getComponents()) c.setBackground(color);
    }

    @Override
    public void updateUI() {
        setUI(new BasicPanelUI());
        if (clickableColors != null) {
            if (!selected) {
                if (interactionListener != null) removeMouseListener(interactionListener);
                interactionListener = TabColorChanger.createTabColorChanger(this, ColorService.forCurrentTheme(clickableColors));
                addMouseListener(interactionListener);
            }
            updateColors();
        }
    }

    private final class HitsLabel extends SamebugLabel {
        private static final int Height = 20;
        private final Font font = FontService.demi(10);

        @Override
        public Dimension getPreferredSize() {
            // Override to guarantee size
            String hits = getText();

            if (hits.length() == 1) {
                // the background is a circle
                return new Dimension(Height, Height);
            } else {
                // the background is a rounded rect
                FontMetrics fm = getFontMetrics(font);
                int width = fm.stringWidth(hits);
                return new Dimension(8 + width + 8, Height);
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
                g.fillRoundRect(1, 1, getWidth() - 2, getHeight() - 2, 20, 20);
            }

            g2.setColor(SamebugTabHeader.this.getBackground());
            g2.setFont(font);

            // TODO this will break when changing the font
            if (hits.length() == 1) {
                g2.drawString(getText(), 7, 13);
            } else {
                g2.drawString(getText(), 8, 13);
            }
        }
    }
}
