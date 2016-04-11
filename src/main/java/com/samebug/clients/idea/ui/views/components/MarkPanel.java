package com.samebug.clients.idea.ui.views.components;

import com.samebug.clients.idea.resources.SamebugBundle;
import com.samebug.clients.idea.resources.SamebugIcons;

import javax.swing.*;
import java.awt.*;

/**
 * Created by poroszd on 4/8/16.
 */
public class MarkPanel extends JPanel {
    public MarkPanel(int score, boolean marked) {
        setLayout(new FlowLayout(FlowLayout.LEFT));
        setBorder(BorderFactory.createEmptyBorder());
        add(new JButton() {
            {
                setText("Mark");
            }
        });
        add(new JPanel(){
            {
                setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 0));
            }
        });
        add(new VoteIcon());
        if (score == 0) {
            add(new JLabel(SamebugBundle.message("samebug.solutions.marked.noone")));
        } else {
            add(new JLabel(SamebugBundle.message("samebug.solutions.marked.anyone", score)));
        }

    }

    class VoteIcon extends JPanel {
        static final int width = 16;
        static final int height = 16;

        public VoteIcon() {
            setPreferredSize(new Dimension(width, height));
        }

        @Override
        public void paintComponent(Graphics g) {
            final Icon tickMark = SamebugIcons.tickMark;
            super.paintComponent(g);
            tickMark.paintIcon(MarkPanel.this, g, 0, 0);
        }

    }
}
