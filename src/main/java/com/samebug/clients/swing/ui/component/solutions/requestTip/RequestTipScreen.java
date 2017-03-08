package com.samebug.clients.swing.ui.component.solutions.requestTip;

import com.samebug.clients.swing.ui.component.util.button.SamebugButton;
import com.samebug.clients.swing.ui.component.util.label.LinkLabel;
import com.samebug.clients.swing.ui.global.MessageService;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public final class RequestTipScreen extends JComponent {
    final WriteRequestArea writeRequestArea;
    final SamebugButton sendButton;
    final LinkLabel cancelButton;

    public RequestTipScreen(final BugmateList bugmateList) {
        writeRequestArea = new WriteRequestArea(bugmateList);
        sendButton = new SamebugButton(MessageService.message("samebug.component.bugmate.ask.send"));
        sendButton.setFilled(true);
        cancelButton = new LinkLabel(MessageService.message("samebug.component.bugmate.ask.cancel"));

        setLayout(new MigLayout("fillx", "0[]0", "0[]10[]10[]0"));
        add(writeRequestArea, "cell 0 0, growx");
        add(sendButton, "cell 0 1, align center");
        add(cancelButton, "cell 0 2, align center");

        sendButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                bugmateList.getListener().askBugmates(bugmateList, "TODO");
            }
        });

        cancelButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                bugmateList.requestTip.changeToClosedState();
            }
        });
    }
}
