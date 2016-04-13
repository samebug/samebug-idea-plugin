package com.samebug.clients.idea.ui.views.components.tip;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Created by poroszd on 4/13/16.
 */
public abstract class WriteTipCTA extends JPanel {
    JComponent ctaButton;

    public void setActionHandler(final ActionHandler actionHandler) {
        ctaButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                actionHandler.onCTAClick();
            }
        });
    }

    public abstract class ActionHandler {
        protected abstract void onCTAClick();
    }

}
