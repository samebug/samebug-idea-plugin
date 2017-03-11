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
import com.samebug.clients.swing.ui.modules.ColorService;
import com.samebug.clients.swing.ui.modules.FontService;

import javax.swing.*;
import javax.swing.plaf.basic.BasicPanelUI;
import java.awt.*;

public abstract class SamebugTabHeader extends JPanel {
    protected final SamebugLabel tabLabel;
    protected final HitsLabel hitsLabel;
    protected boolean selected;
    protected TabColorChanger interactionListener;
    private Color[] selectedColor;
    private Color[] selectedHitColor;

    public SamebugTabHeader(String tabName, int hits) {
        tabLabel = new SamebugLabel(tabName, FontService.demi(16));
        hitsLabel = new HitsLabel(HitsLabel.SMALL);
        hitsLabel.setText(Integer.toString(hits));
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
            interactionListener = TabColorChanger.createTabColorChanger(this, ColorService.LinkInteraction);
            addMouseListener(interactionListener);
        }
        updateColors();
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
        if (!selected) {
            if (interactionListener != null) removeMouseListener(interactionListener);
            interactionListener = TabColorChanger.createTabColorChanger(this, ColorService.LinkInteraction);
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
        else foreground = interactionListener.getColor();
        setForeground(foreground);
        setBackground(ColorService.forCurrentTheme(ColorService.Background));

        // hit label in selected state has a visually corrected color
        if (hitsLabel != null && selected) hitsLabel.setForeground(ColorService.forCurrentTheme(selectedHitColor));
    }


}
