package com.samebug.clients.idea.ui.component.util;

import com.samebug.clients.idea.ui.DrawUtil;
import com.samebug.clients.idea.ui.ImageUtil;

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
        if (avatarUrl == null) this.avatar = ImageUtil.getAvatarPlaceholder(size, size);
        else {
            BufferedImage imageFromUrl = ImageUtil.getScaled(avatarUrl, size, size);
            if (imageFromUrl == null) this.avatar = ImageUtil.getAvatarPlaceholder(size, size);
            else this.avatar = imageFromUrl;
        }
        setPreferredSize(new Dimension(size, size));
    }

    @Override
    public void paintComponent(Graphics g) {
        Graphics2D g2 = DrawUtil.init(g);
        // NOTE while we want to clip the images to circle shape, Graphics2D.clip does not handle anti-aliasing, so we use texture filling
        Shape clip = new Ellipse2D.Float(0, 0, size, size);
        Rectangle patternRect = new Rectangle(0, 0, size, size);
        g2.setPaint(new TexturePaint(avatar, patternRect));
        g2.fill(clip);
    }
}
