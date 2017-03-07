package com.samebug.clients.swing.ui.component.solutions.requestTip;

import com.samebug.clients.swing.ui.component.solutions.BugmateList;
import com.samebug.clients.swing.ui.component.util.button.SamebugButton;
import com.samebug.clients.swing.ui.global.MessageService;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public final class RequestTipCTAScreen extends SamebugButton {
    public RequestTipCTAScreen(final RequestTip parent) {
        setText(MessageService.message("samebug.component.bugmate.list.ask"));
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                parent.changeToOpenState();
            }
        });

    }
}