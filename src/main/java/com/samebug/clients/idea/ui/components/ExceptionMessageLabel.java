package com.samebug.clients.idea.ui.components;

import org.apache.commons.lang.StringEscapeUtils;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/**
 * Created by poroszd on 4/1/16.
 */
public class ExceptionMessageLabel extends JLabel {
    public ExceptionMessageLabel(@Nullable String message) {
        {
            final String escapedText;
            if (message == null) {
                escapedText = String.format("<html><i>No message provided</i></html>");
            } else {
                // Escape html, but keep line breaks
                String broken = StringEscapeUtils.escapeHtml(message).replaceAll("\\n", "<br>");
                escapedText = String.format("<html>%s</html>", broken);
            }

            setFont(UIManager.getFont("TextArea.font"));
            setText(escapedText);
            setVerticalAlignment(SwingConstants.TOP);
        }
    }
}
