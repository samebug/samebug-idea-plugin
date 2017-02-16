package com.samebug.clients.idea.ui.component.solutions;

import com.samebug.clients.idea.ui.ColorUtil;
import com.samebug.clients.idea.ui.DrawUtil;
import com.samebug.clients.idea.ui.component.util.panel.TransparentPanel;

import javax.swing.*;
import java.awt.*;

public class HelpOthersCTA extends TransparentPanel {

    {
    }

    @Override
    public void paintBorder(Graphics g) {
        Graphics2D g2 = DrawUtil.init(g);

        g2.setColor(ColorUtil.separator());
        g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 5, 5);
    }

    public final static class Model {
        protected final int usersWaitingHelp;

        public Model(Model rhs) {
            this(rhs.usersWaitingHelp);
        }

        public Model(int usersWaitingHelp) {
            this.usersWaitingHelp = usersWaitingHelp;
        }
    }
}
