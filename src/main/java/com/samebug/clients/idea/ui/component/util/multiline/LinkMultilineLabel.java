package com.samebug.clients.idea.ui.component.util.multiline;

import com.samebug.clients.idea.ui.ColorUtil;
import com.samebug.clients.idea.ui.FontRegistry;
import com.samebug.clients.idea.ui.component.util.interaction.Colors;
import com.samebug.clients.idea.ui.component.util.interaction.InteractiveComponent;

import javax.swing.*;
import java.awt.*;

public class LinkMultilineLabel extends JTextArea {
    private Colors[] foregroundColors;
    private InteractiveComponent interactiveComponent;

    {
        setEditable(false);
        setFocusable(false);
        setWrapStyleWord(true);
        setLineWrap(true);

        setForeground(ColorUtil.LinkInteraction);
        setFont(new Font(FontRegistry.AvenirRegular, Font.PLAIN, 16));
        setOpaque(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        updateUI();
    }

    public void setForeground(Colors[] c) {
        foregroundColors = c;
        super.setForeground(ColorUtil.forCurrentTheme(foregroundColors).normal);
    }

    @Override
    public void updateUI() {
        super.updateUI();
        interactiveComponent = InteractiveComponent.updateForegroundInteraction(interactiveComponent, ColorUtil.forCurrentTheme(foregroundColors), this);
    }
}