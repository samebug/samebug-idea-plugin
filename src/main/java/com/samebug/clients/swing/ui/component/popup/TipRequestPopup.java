package com.samebug.clients.swing.ui.component.popup;

import com.samebug.clients.common.ui.component.popup.ITipRequestPopup;
import com.samebug.clients.swing.ui.base.button.SamebugButton;
import com.samebug.clients.swing.ui.base.label.LinkLabel;
import com.samebug.clients.swing.ui.base.label.SamebugLabel;
import com.samebug.clients.swing.ui.base.multiline.SamebugMultilineLabel;
import com.samebug.clients.swing.ui.base.panel.SamebugPanel;
import com.samebug.clients.swing.ui.component.profile.AvatarIcon;
import com.samebug.clients.swing.ui.modules.ColorService;
import com.samebug.clients.swing.ui.modules.FontService;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public final class TipRequestPopup extends SamebugPanel implements ITipRequestPopup {
    public TipRequestPopup(Model model) {
        final JComponent avatar = new AvatarIcon(model.avatarUrl, 40);
        final JComponent title = new SamebugLabel(model.displayName + " sent a tip request", FontService.demi(14));
        final JComponent body = new RequestBody(model.tipRequestBody);
        final JComponent answer = new AnswerButton();
        final JComponent later = new LaterButton();

        setBackground(ColorService.Tip);
        setLayout(new MigLayout("", "10[40!]8[fill]10", "10[]5[]10[]10"));
        add(avatar, "cell 0 0, spany 2");
        add(title, "cell 1 0");
        add(body, "cell 1 1");
        add(answer, "cell 1 2");
        add(later, "cell 1 2");
    }

    final class RequestBody extends SamebugMultilineLabel {
        public RequestBody(String body) {
            setText(body);
        }
    }

    final class AnswerButton extends SamebugButton {
        {
            setText("Answer");
            setFont(FontService.demi(14));
            setFilled(true);
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    super.mouseClicked(e);
                }
            });
        }
    }

    final class LaterButton extends LinkLabel {
        {
            setText("Later");
            setFont(FontService.demi(14));
            setBorder(BorderFactory.createEmptyBorder(0,10,0,10));
        }
    }
}
