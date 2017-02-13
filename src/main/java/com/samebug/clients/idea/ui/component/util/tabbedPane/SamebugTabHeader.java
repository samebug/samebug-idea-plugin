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

        add(tabLabel, "cell 0 0");
        add(hitsLabel, "cell 1 0");
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
        if (selected) {
            tabLabel.setForeground(ColorUtil.text());
            hitsLabel.setBackground(ColorUtil.selectedTab());
        } else {
            tabLabel.setForeground(ColorUtil.samebug());
            hitsLabel.setBackground(ColorUtil.samebug());
        }
    }

    private final class HitsLabel extends JLabel {
        private static final int Height = 20;
        private final Font font = new Font(FontRegistry.AvenirDemi, Font.PLAIN, 10);

        @Override
        public Dimension getPreferredSize() {
            // Override to guarantee size
            String hits = getText();

            if (hits.length() == 1) {
                // the background is a circle
                return new Dimension(Height, Height);
            } else {
                // the background is a rounded rect
                FontMetrics fm = getFontMetrics(font);
                int width = fm.stringWidth(hits);
                return new Dimension(8 + width + 8, Height);
            }
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
                g.fillOval(1, 1, getWidth() - 2, getHeight() - 2);
            } else {
                g.fillRoundRect(1, 1, getWidth() - 2, getHeight() - 2, 20, 20);
            }

            g2.setColor(ColorUtil.background());
            g2.setFont(font);

            // TODO this will break when changing the font
            if (hits.length() == 1) {
                g2.drawString(getText(), 7, 13);
            } else {
                g2.drawString(getText(), 8, 13);
            }
        }
    }
}
