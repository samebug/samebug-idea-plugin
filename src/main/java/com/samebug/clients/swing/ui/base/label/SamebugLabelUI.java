/*
 * Copyright 2018 Samebug, Inc.
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
package com.samebug.clients.swing.ui.base.label;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.plaf.basic.BasicLabelUI;
import java.awt.*;

public final class SamebugLabelUI extends BasicLabelUI {
    @Override
    public void paint(Graphics g, JComponent c) {
        super.paint(g, c);
    }

    public Dimension getPreferredSize(JComponent c) {
        JLabel label = (JLabel) c;
        String text = label.getText();
        Insets insets = label.getInsets(null);

        int dx = insets.left + insets.right;
        int dy = insets.top + insets.bottom;
        Dimension textBounds = stringBounds(label, text);
        return new Dimension(textBounds.width + dx, textBounds.height + dy);
    }

    /**
     * Honestly, I have no idea what's happening here.
     * <p>
     * Empirically I found out that on retina display, running Oracle JVM the component and graphics font metrics return different string width for some strings.
     * There are some related JDK bugs, but those are claimed to be fixed long ago.
     * Maybe it's related to fractional font metrics, or subpixel aliasing, or whatever.
     * <p>
     * TODO is there a better way to workaround this?
     * TODO do we need a similar workaround for multiline labels, or buttons, text fields, anywhere?
     * TODO does this workaround have bad performance impact?
     */
    public static Dimension stringBounds(@NotNull JComponent component, @Nullable String text) {
        if (text == null) return new Dimension(0, 0);

        Font font = component.getFont();
        if (font == null) return new Dimension(0, 0);

        Graphics componentGraphics = component.getGraphics();
        if (componentGraphics == null) return new Dimension(0, 0);

        FontMetrics componentFontMetric = component.getFontMetrics(font);
        if (componentFontMetric == null) return new Dimension(0, 0);

        FontMetrics graphicsFontMetric = componentGraphics.getFontMetrics(font);
        if (graphicsFontMetric == null) return new Dimension(0, 0);

        int textWidthAccordingToComponent = componentFontMetric.stringWidth(text);
        int textWidthAccordingToGraphics = graphicsFontMetric.stringWidth(text);
        int width = Math.max(textWidthAccordingToGraphics, textWidthAccordingToComponent);
        int height = componentFontMetric.getHeight();
        return new Dimension(width, height);
    }
}
