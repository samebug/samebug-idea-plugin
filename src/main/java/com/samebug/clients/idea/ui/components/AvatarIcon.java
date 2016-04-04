package com.samebug.clients.idea.ui.components;

import javax.swing.*;
import java.awt.*;

/**z
 * Created by poroszd on 4/4/16.
 */
public class AvatarIcon extends JPanel {
    final Image iconImage;
    static final int iconWidth = 64;
    static final int iconHeight = 64;
    static final int width = 64;
    static final int height = 64;

    public AvatarIcon(Image image) {
        iconImage = image;
        setPreferredSize(new Dimension(width, height));
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.drawImage(iconImage, 0, 0, null, null);
    }
}
