package com.samebug.clients.swing.ui.component.solutions.requestTip;

import com.samebug.clients.swing.ui.component.util.button.SamebugButton;
import com.samebug.clients.swing.ui.component.util.label.LinkLabel;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public final class RequestTipScreen extends JComponent {
    final SamebugButton sendButton;
    final LinkLabel cancelButton;

    public RequestTipScreen(final RequestTip parent) {
        sendButton = new SamebugButton("Send");
        cancelButton = new LinkLabel("Cancel");

        setLayout(new MigLayout("", "", ""));
        add(sendButton, "cell 0 0");
        add(cancelButton, "cell 0 1");

        sendButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                parent.getListener().askBugmates(parent, "TODO");
            }
        });

        cancelButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                parent.changeToClosedState();
            }
        });
    }
}
