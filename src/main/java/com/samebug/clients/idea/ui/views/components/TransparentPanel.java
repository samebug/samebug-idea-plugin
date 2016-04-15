package com.samebug.clients.idea.ui.views.components;

import javax.swing.*;
import java.awt.*;

/**
 * Created by poroszd on 4/15/16.
 */
public class TransparentPanel extends JPanel {
    {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder());
        setOpaque(false);
    }
}
