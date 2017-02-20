package com.samebug.clients.idea.ui.component.profile;

import com.samebug.clients.idea.ui.ColorUtil;
import com.samebug.clients.idea.ui.SamebugBundle;
import com.samebug.clients.idea.ui.component.util.interaction.Colors;
import com.samebug.clients.idea.ui.component.util.interaction.ForegroundColorChanger;

import java.awt.*;

public final class MessageLabel extends NumberLabel {
    private Colors[] foregroundColors;
    private ForegroundColorChanger interactionListener;

    public MessageLabel(int nMessages) {
        super(nMessages, SamebugBundle.message("samebug.component.profile.messages.label"));

        setForeground(ColorUtil.SecondaryLinkInteraction);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        updateUI();
    }

    public void setForeground(Colors[] c) {
        foregroundColors = c;
        setForeground(ColorUtil.forCurrentTheme(foregroundColors).normal);
    }

    @Override
    public void updateUI() {
        super.updateUI();
        interactionListener = ForegroundColorChanger.updateForegroundInteraction(interactionListener, ColorUtil.forCurrentTheme(foregroundColors), this);
    }
}
