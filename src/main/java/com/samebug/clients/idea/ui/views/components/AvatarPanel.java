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

import com.samebug.clients.idea.ui.ImageUtil;
import com.samebug.clients.search.api.entities.legacy.Author;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.font.TextAttribute;
import java.io.IOException;
import java.util.HashMap;

/**
 * Created by poroszd on 4/12/16.
 */
public class AvatarPanel extends TransparentPanel {
    final static Image avatarPlaceholder;

    static {
        Image tmpImage;
        try {
            tmpImage = ImageIO.read(AvatarPanel.class.getClassLoader().getResource("/com/samebug/avatar-placeholder.png"));
        } catch (IOException e) {
            tmpImage = null;
        }
        avatarPlaceholder = tmpImage;
    }

    public AvatarPanel(final Author author) {
        // 74 width comes by 5 + 64 + 5 from the border
        setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
        setPreferredSize(new Dimension(74, 100));
        final Image profile = ImageUtil.getScaled(author.avatarUrl, 64, 64);
        add(new AvatarIcon(profile != null ? profile : avatarPlaceholder), BorderLayout.NORTH);
        add(new LinkLabel(author.name, author.url) {
            {
                HashMap<TextAttribute, Object> attributes = new HashMap<TextAttribute, Object>();
                attributes.put(TextAttribute.SIZE, 10);
                setFont(getFont().deriveFont(attributes));
                setHorizontalAlignment(SwingConstants.CENTER);
                setHorizontalTextPosition(SwingConstants.CENTER);
            }
        }, BorderLayout.CENTER);
    }
}
