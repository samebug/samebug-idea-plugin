package com.samebug.clients.idea.ui.component;

import com.samebug.clients.common.ui.Colors;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.font.TextAttribute;
import java.util.HashMap;

public class ErrorLabel extends JLabel {
    public ErrorLabel(@NotNull String message) {
        final HashMap<TextAttribute, Object> attributes = new HashMap<TextAttribute, Object>();
        attributes.put(TextAttribute.SIZE, 12);
        attributes.put(TextAttribute.WEIGHT, TextAttribute.WEIGHT_BOLD);
        setFont(getFont().deriveFont(attributes));
        setText(message);
        setForeground(Colors.samebugWhite);
    }

}
