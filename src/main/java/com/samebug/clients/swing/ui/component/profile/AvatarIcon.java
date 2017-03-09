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

import com.samebug.clients.swing.ui.modules.DrawService;
import com.samebug.clients.swing.ui.modules.WebImageService;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.net.URL;

public final class AvatarIcon extends JPanel {
    private final BufferedImage avatar;
    private final int size;

    public AvatarIcon(URL avatarUrl, int size) {
        this.size = size;
        setOpaque(false);
        // TODO handle avatar loading problems (here or when creating the model)
        if (avatarUrl == null) this.avatar = WebImageService.getAvatarPlaceholder(size, size);
        else {
            BufferedImage imageFromUrl = WebImageService.getScaled(avatarUrl, size, size);
            if (imageFromUrl == null) this.avatar = WebImageService.getAvatarPlaceholder(size, size);
            else this.avatar = imageFromUrl;
        }
        setMinimumSize(new Dimension(size, size));
        setPreferredSize(new Dimension(size, size));
        setMaximumSize(new Dimension(size, size));
    }

    @Override
    public void paintComponent(Graphics g) {
        Graphics2D g2 = DrawService.init(g);
        // NOTE while we want to clip the images to circle shape, Graphics2D.clip does not handle anti-aliasing, so we use texture filling
        Shape clip = new Ellipse2D.Float(0, 0, size, size);
        Rectangle patternRect = new Rectangle(0, 0, size, size);
        g2.setPaint(new TexturePaint(avatar, patternRect));
        g2.fill(clip);
    }
}
