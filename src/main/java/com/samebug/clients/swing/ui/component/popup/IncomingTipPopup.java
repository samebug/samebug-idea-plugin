package com.samebug.clients.swing.ui.component.popup;

import com.samebug.clients.common.ui.component.popup.IIncomingTipPopup;
import com.samebug.clients.swing.ui.base.label.SamebugLabel;
import com.samebug.clients.swing.ui.base.multiline.SamebugMultilineLabel;
import com.samebug.clients.swing.ui.base.panel.SamebugPanel;
import com.samebug.clients.swing.ui.component.profile.AvatarIcon;
import com.samebug.clients.swing.ui.modules.ColorService;
import com.samebug.clients.swing.ui.modules.FontService;
import com.samebug.clients.swing.ui.modules.MessageService;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.text.MessageFormat;

public final class IncomingTipPopup extends SamebugPanel implements IIncomingTipPopup {
    private final static int AvatarSize = 40;

    public IncomingTipPopup(Model model) {
        final JComponent avatar = new AvatarIcon(model.avatarUrl, AvatarSize);
        final SamebugLabel title = new SamebugLabel(MessageService.message("samebug.component.tip.incoming.title", model.displayName), FontService.demi(14));
        final TipBody body = new TipBody(model.tipBody);

        setBackgroundColor(ColorService.Tip);
        title.setForegroundColor(ColorService.EmphasizedText);
        body.setForegroundColor(ColorService.EmphasizedText);

        setLayout(new MigLayout("", MessageFormat.format("10px[{0}px!]8px[320px]10px", AvatarSize), "10px[]5px[]10px"));
        add(avatar, "cell 0 0, spany 2, align center top");
        add(title, "cell 1 0");
        add(body, "cell 1 1, growx, wmin 0");
    }

    final class TipBody extends SamebugMultilineLabel {
        public TipBody(String body) {
            setText(body);
            setFont(FontService.regular(14));
        }
    }
}
