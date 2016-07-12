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
package com.samebug.clients.idea.ui.component;

import com.samebug.clients.idea.ui.ImageUtil;
import com.samebug.clients.search.api.entities.Author;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.font.TextAttribute;
import java.util.HashMap;

public class AvatarPanel extends TransparentPanel {
    public AvatarPanel(@NotNull final Author author) {
        setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
        // 74 width comes by 5 + 64 + 5 from the border
        setPreferredSize(new Dimension(74, 100));
        final Image profile = author.getAvatarUrl() == null ? null : ImageUtil.getScaled(author.getAvatarUrl(), 64, 64);
        add(new AvatarIcon(profile != null ? profile : ImageUtil.getAvatarPlaceholder()), BorderLayout.NORTH);
        // TODO: use author.url when the profile page will be public
        add(new LinkLabel(author.getName(), null) {
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
