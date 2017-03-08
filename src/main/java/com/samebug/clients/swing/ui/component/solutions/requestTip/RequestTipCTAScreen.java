package com.samebug.clients.swing.ui.component.solutions.requestTip;

import com.samebug.clients.swing.ui.component.util.button.SamebugButton;
import com.samebug.clients.swing.ui.component.util.panel.TransparentPanel;
import com.samebug.clients.swing.ui.global.MessageService;
import net.miginfocom.swing.MigLayout;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public final class RequestTipCTAScreen extends TransparentPanel {
    final BugmateList bugmateList;

    public RequestTipCTAScreen(BugmateList bugmateList) {
        this.bugmateList = bugmateList;

        final AskButton ask = new AskButton();

        setLayout(new MigLayout("", ":push[]:push", ""));
        add(ask);
    }

    final class AskButton extends SamebugButton {
        {
            setText(MessageService.message("samebug.component.bugmate.list.ask"));
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    bugmateList.requestTip.changeToOpenState();
                }
            });

        }
    }
}