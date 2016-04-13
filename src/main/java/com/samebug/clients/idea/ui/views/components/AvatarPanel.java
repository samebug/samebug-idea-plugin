package com.samebug.clients.idea.ui.views.components;

import com.samebug.clients.idea.ui.ImageUtil;
import com.samebug.clients.search.api.entities.legacy.Author;

import javax.swing.*;
import java.awt.*;
import java.awt.font.TextAttribute;
import java.util.HashMap;

/**
 * Created by poroszd on 4/12/16.
 */
public class AvatarPanel extends JPanel {
    public AvatarPanel(final Author author) {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder());
        setOpaque(false);
        // TODO 74 width comes by 5 + 64 + 5 from AvatarIcon's border
        setPreferredSize(new Dimension(74, 100));
        final Image profile = ImageUtil.getScaled(author.avatarUrl, 64, 64);
        add(new AvatarIcon(profile), BorderLayout.NORTH);
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
