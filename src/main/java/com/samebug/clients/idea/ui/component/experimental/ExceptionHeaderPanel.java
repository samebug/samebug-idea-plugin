package com.samebug.clients.idea.ui.component.experimental;

import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;

public class ExceptionHeaderPanel extends JPanel {
    MultiLineLabel title;

    public ExceptionHeaderPanel() {
        title = new MultiLineLabel("CursorIndexOutOfBoundsException: Index 0 requested, with a size of 0") {
            {
                setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 20));
                setForeground(Constants.TextColor);
                setFont(new Font(Constants.AvenirDemi, Font.PLAIN, 24));
            }

            @Override
            public Dimension getPreferredSize() {
                if (getLineCount() <= 1) {
                    return new Dimension(Integer.MAX_VALUE, 24 + 2);
                } else {
                    return new Dimension(Integer.MAX_VALUE, 24 * 2 + 8);
                }
            }
        };

        setBorder(null);
        setBackground(Color.white);
        setLayout(new MigLayout(
                "fillx, filly",
                "0[]0",
                "30[]30"
        ));

        add(title, "wmin 0, hmax 56");
    }

}

