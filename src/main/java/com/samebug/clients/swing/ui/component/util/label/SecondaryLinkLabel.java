package com.samebug.clients.swing.ui.component.util.label;

import com.samebug.clients.swing.ui.ColorUtil;
import com.samebug.clients.swing.ui.FontRegistry;
import com.samebug.clients.swing.ui.component.util.interaction.Colors;
import com.samebug.clients.swing.ui.component.util.interaction.ForegroundColorChanger;

import javax.swing.*;
import java.awt.*;

public class SecondaryLinkLabel extends JLabel {
    private Colors[] foregroundColors;
    private ForegroundColorChanger interactionListener;

    public SecondaryLinkLabel() {
        this(null);
    }

    public SecondaryLinkLabel(String text) {
        this(text, FontRegistry.regular(16));
    }

    public SecondaryLinkLabel(String text, Font font) {
        super(text);
        setForeground(ColorUtil.SecondaryLinkInteraction);
        setFont(font);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        updateUI();
    }

    public void setForeground(Colors[] c) {
        foregroundColors = c;
        setForeground(ColorUtil.forCurrentTheme(foregroundColors).normal);
    }

    @Override
    public void updateUI() {
        setUI(new SamebugLabelUI());
        interactionListener = ForegroundColorChanger.updateForegroundInteraction(interactionListener, ColorUtil.forCurrentTheme(foregroundColors), this);
    }
}
