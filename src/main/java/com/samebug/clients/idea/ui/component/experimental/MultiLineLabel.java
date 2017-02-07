package com.samebug.clients.idea.ui.component.experimental;

import javax.swing.*;

public class MultiLineLabel extends JTextArea {
    public MultiLineLabel() {
        super();
        setEditable(false);
        setCursor(null);
        setFocusable(false);
        setWrapStyleWord(true);
        setLineWrap(true);

        setFont(UIManager.getFont("Label.font"));
        setOpaque(false);
    }

    // TODO remove
    public MultiLineLabel(String text){
        super(text);
        setEditable(false);
        setCursor(null);
        setFocusable(false);
        setWrapStyleWord(true);
        setLineWrap(true);

        setFont(UIManager.getFont("Label.font"));
        setOpaque(false);
    }
}
