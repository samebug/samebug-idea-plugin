package com.samebug.clients.idea.ui.component.experimental;

import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;

public class MarkPanel extends JPanel {
    JLabel counter;
    JButton button;

    public MarkPanel() {
        counter = new JLabel("555") {
            {
                setBorder(null);
                setForeground(Constants.TextColor);
                setBackground(Constants.MarkPanelBackgroundColor);
                setOpaque(true);
                setHorizontalAlignment(SwingConstants.CENTER);
                setFont(new Font(Constants.AvenirRegular, Font.PLAIN, 14));
            }
        };

        button = new JButton("Mark as helpful") {
            {
                setBorder(null);
                setContentAreaFilled(false);
                setForeground(Constants.TextColor);
                setBackground(Constants.MarkPanelBackgroundColor);
                setOpaque(true);
                setFont(new Font(Constants.AvenirRegular, Font.PLAIN, 14));
            }
        };

        setBorder(null);
        setBackground(Color.white);
        setLayout(new MigLayout(
                "",
                "0[]1[]0",
                "0[]0"));
        add(counter, "w 40!, h 40!");
        add(button, "w 120!, h 40!");
    }
}
