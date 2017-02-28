package com.samebug.clients.swing.ui.component.util.errorBarPane;

import com.samebug.clients.swing.ui.ColorUtil;
import com.samebug.clients.swing.ui.SamebugIcons;
import com.samebug.clients.swing.ui.component.util.panel.SamebugPanel;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;

public class ErrorBar extends SamebugPanel {
    public ErrorBar(String text) {
        setBackground(ColorUtil.ErrorBar);

        // TODO shadow border
        setLayout(new MigLayout("", "20[]10[]20", "15[]15"));
        final JLabel alertIcon = new JLabel(SamebugIcons.alertErrorBar());
        final JLabel message = new JLabel(text);
        add(alertIcon, "cell 0 0");
        add(message, "cell 1 0");
    }
}
