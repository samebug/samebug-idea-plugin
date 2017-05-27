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

import com.samebug.clients.swing.ui.base.animation.*;
import com.samebug.clients.swing.ui.modules.ColorService;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.plaf.basic.BasicPanelUI;
import java.awt.*;

public abstract class SamebugTabHeader extends JPanel implements IAnimatedComponent {
    protected boolean selected;
    protected TabColorChanger interactionListener;
    protected Color[] selectedColor;

    @NotNull
    private final ComponentAnimationController myAnimationController;

    public SamebugTabHeader() {
        myAnimationController = new ComponentAnimationController(this);
        selectedColor = ColorService.Text;
        setOpaque(false);
        notSelectedState();
    }

    public void setSelected(boolean selected) {
        boolean wasSelected = this.selected;
        this.selected = selected;

        if (!wasSelected && selected && interactionListener != null) selectedState();
        else if (wasSelected && !selected) notSelectedState();
    }

    /**
     * Update the current foreground color for this and child components.
     * NOTE: this is mostly for the interaction listener, to handle color change from rollover and pressed events
     */
    @Override
    public void setForeground(Color color) {
        super.setForeground(color);
        setChildrenForeground(color);
    }

    @Override
    public void setBackground(Color color) {
        super.setBackground(color);
        setChildrenBackground(color);
    }

    @Override
    public void updateUI() {
        setUI(new BasicPanelUI());
        updateColors();
    }

    @Override
    public void paint(Graphics g) {
        myAnimationController.paint(g);
    }

    @Override
    public void paintOriginalComponent(Graphics g) {
        super.paint(g);
    }

    /**
     * Update foreground and background color of this component based on the current theme
     */
    protected void updateColors() {
        if (selected) setForeground(ColorService.forCurrentTheme(selectedColor));
        else if (interactionListener != null) setForeground(interactionListener.getColor());
        setBackground(ColorService.forCurrentTheme(ColorService.Background));
    }

    protected void setChildrenForeground(Color foreground) {
        for (Component c : getComponents()) c.setForeground(foreground);
    }

    protected void setChildrenBackground(Color background) {
        for (Component c : getComponents()) c.setBackground(background);
    }

    protected void selectedState() {
        this.removeMouseListener(interactionListener);
        interactionListener = null;
        updateColors();
    }

    protected void notSelectedState() {
        interactionListener = TabColorChanger.createTabColorChanger(this, ColorService.LinkInteraction);
        addMouseListener(interactionListener);
        updateColors();
    }

    /**
     * TODO this does not actually changes the selected property of the tab header
     */
    public ControllableAnimation animatedSetSelected(boolean selected, int totalFrames) {
        PaintableAnimation myAnimation = new FadeAnimation(selected, totalFrames);
        myAnimationController.prepareNewAnimation(myAnimation);
        return myAnimation;
    }

    private final class FadeAnimation extends PaintableAnimation {
        final Color[] gradient;

        FadeAnimation(boolean makeSelected, int totalFrames) {
            super(totalFrames);
            if (makeSelected) {
                gradient = Sampler.gradient(ColorService.forCurrentTheme(ColorService.LinkInteraction.normal), ColorService.forCurrentTheme(selectedColor), totalFrames + 1);
            } else {
                gradient = Sampler.gradient(ColorService.forCurrentTheme(selectedColor), ColorService.forCurrentTheme(ColorService.LinkInteraction.normal), totalFrames + 1);
            }
        }

        @Override
        protected void doStart() {
        }

        @Override
        protected void doSetFrame(int frame) {
            setForeground(gradient[frame]);
        }

        @Override
        protected void doPaint(Graphics g) {
            SamebugTabHeader.this.paint(g);
        }

        @Override
        protected void doFinish() {
            updateColors();
        }
    }
}
