package com.samebug.clients.swing.ui.component.bugmate;

import com.samebug.clients.common.api.entities.helpRequest.MyHelpRequest;
import com.samebug.clients.common.ui.modules.TextService;
import com.samebug.clients.swing.ui.base.button.SamebugButton;
import com.samebug.clients.swing.ui.base.label.SamebugLabel;
import com.samebug.clients.swing.ui.base.panel.SamebugPanel;
import com.samebug.clients.swing.ui.modules.ColorService;
import com.samebug.clients.swing.ui.modules.FontService;
import com.samebug.clients.swing.ui.modules.MessageService;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public final class RevokeHelpRequest extends JComponent {
    final BugmateList bugmateList;

    private final JComponent helpRequestLabel;
    private final SamebugButton revoke;

    public RevokeHelpRequest(BugmateList bugmateList) {
        this.bugmateList = bugmateList;
        helpRequestLabel = new HelpRequestLabel(bugmateList.model.helpRequest);
        revoke = new RevokeButton(bugmateList.model.helpRequest);

        setLayout(new MigLayout("fillx", "0[]0", "0[]15[]0"));
        add(helpRequestLabel, "cell 0 0, growx");
        add(revoke, "cell 0 1, align center");
    }

    public void startRevoke() {
        // TODO loading animation
        revoke.setText("loading...");
    }

    public void failRevoke() {
        // TODO reset

    }

    public void successRevoke() {
        // TODO reset
    }

    private final class HelpRequestLabel extends SamebugPanel {
        public HelpRequestLabel(MyHelpRequest helpRequest) {
            final SamebugLabel text = new SamebugLabel(format(helpRequest));
            text.setFont(FontService.regular(16));
            text.setForegroundColor(ColorService.TipText);
            text.setHorizontalAlignment(SwingConstants.CENTER);
            setBackgroundColor(ColorService.Tip);

            setLayout(new MigLayout("fillx", "30[fill]30", "30[]30"));
            add(text, "al center");
        }

         String format(MyHelpRequest r) {
            // TODO it is not exactly the same as in the design
            String date = TextService.adaptiveTimestamp(r.createdAt);
            return MessageService.message("samebug.component.helpRequest.ask.alreadySent", date);
        }
    }

    private final class RevokeButton extends SamebugButton {
        public RevokeButton(final MyHelpRequest helpRequest) {
            setText(MessageService.message("samebug.component.helpRequest.ask.revoke"));
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    bugmateList.getListener().revokeHelpRequest(bugmateList, helpRequest.id);
                }
            });
        }
    }
}
