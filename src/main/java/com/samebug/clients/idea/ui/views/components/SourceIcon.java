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
package com.samebug.clients.idea.ui.views.components;

import com.samebug.clients.idea.ui.ColorUtil;

import javax.swing.*;
import java.awt.*;

/**
 * Created by poroszd on 4/1/16.
 */
public class SourceIcon extends TransparentPanel {
    final Image iconImage;
    static final int width = 32;
    static final int height = 32;

    public SourceIcon(Image image) {
        iconImage = image;
        setPreferredSize(new Dimension(width, height));
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        Color iconBackgroundColor = ColorUtil.sourceIconBackground();
        if (iconBackgroundColor != null) {
            g2.setColor(iconBackgroundColor);
            g2.fillOval(0, 0, width, height);
        }
        g2.drawImage(iconImage, 2, 0, null, null);
    }
}
