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
package com.samebug.clients.idea.ui.components;

import javax.swing.*;
import java.awt.*;

/**
 * Created by poroszd on 4/4/16.
 */
public class AvatarIcon extends JPanel {
    final Image avatar;
    static final int width = 64;
    static final int height = 64;

    public AvatarIcon(Image image) {
        this.avatar = image;
        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
        setPreferredSize(new Dimension(width, height));
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.drawImage(avatar, 0, 0, null, null);
    }
}
