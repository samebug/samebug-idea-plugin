package com.samebug.clients.idea.ui.components;

import javax.swing.*;
import java.awt.*;

/**
 * Created by poroszd on 3/30/16.
 */
public class ImagePanel extends JPanel {

    private Image image;

    public ImagePanel(Image image) {
        this.image = image;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(image, 0, 0, null);
    }

}