package com.samebug.clients.idea.ui.component.util.tabbedPane;

import com.samebug.clients.idea.ui.ColorUtil;
import com.samebug.clients.idea.ui.DrawUtil;
import com.samebug.clients.idea.ui.FontRegistry;
import com.samebug.clients.idea.ui.component.util.SamebugLabel;

import javax.swing.*;
import java.awt.*;

public abstract class SamebugTabHeader extends JPanel {
    protected final SamebugLabel tabLabel;
    protected final HitsLabel hitsLabel;
    protected boolean selected;

    public SamebugTabHeader(String tabName, int hits) {
        tabLabel = new SamebugLabel(tabName, FontRegistry.AvenirDemi, 16);
        hitsLabel = new HitsLabel();
        hitsLabel.setText(Integer.toString(hits));

        setOpaque(false);
        // NOTE the layout is specified in the derived classes, don't forget to introduce changes to both when necessary

        add(tabLabel);
        add(hitsLabel);
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
        if (selected) {
            tabLabel.setForeground(ColorUtil.text());
            hitsLabel.setBackground(ColorUtil.text());
        } else {
            tabLabel.setForeground(ColorUtil.samebug());
            hitsLabel.setBackground(ColorUtil.samebug());
        }
    }

    private final class HitsLabel extends JLabel {
        private final Font font = new Font(FontRegistry.AvenirDemi, Font.PLAIN, 10);

        {
            setBorder(BorderFactory.createEmptyBorder(2, 6, 2, 6));
        }

        @Override
        public void paint(Graphics g) {
            Graphics2D g2 = DrawUtil.init(g);

            // NOTE different behaviour for one and for more digits
            // For one digit, we have a disk as background, and show the number in the center
            // For more digit, we have a rounded rectangle, and push the number to the right (to be in the center of the rectangle
            String hits = getText();


            // TODO: this is dirty, it would be better to draw only the background without the text
            g2.setColor(getBackground());
            if (hits.length() == 1) {
                g.fillOval(0, 1, getWidth() - 1, getHeight() - 1);
            } else {
                g.fillRoundRect(0, 1, getWidth() - 1, getHeight() - 1, 20, 20);
            }

            g2.setColor(ColorUtil.background());
            g2.setFont(font);

            // TODO this will break when changing the font
            if (hits.length() == 1) {
                g2.drawString(getText(), 6, 13);
            } else {
                g2.drawString(getText(), 8, 13);
            }
        }
    }
}
