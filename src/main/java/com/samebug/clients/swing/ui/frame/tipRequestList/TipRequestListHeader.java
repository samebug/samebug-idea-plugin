package com.samebug.clients.swing.ui.frame.tipRequestList;

import com.samebug.clients.common.ui.frame.tipRequestList.ITipRequestListHeader;
import com.samebug.clients.swing.ui.base.label.SamebugLabel;
import com.samebug.clients.swing.ui.modules.ColorService;
import com.samebug.clients.swing.ui.modules.FontService;
import com.samebug.clients.swing.ui.modules.MessageService;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;

// TODO extract HitsLabel from tabbed pane and use that here for the number
public final class TipRequestListHeader extends JComponent implements ITipRequestListHeader {
    public TipRequestListHeader(Model model) {
        final SamebugLabel title = new SamebugLabel(MessageService.message("samebug.frame.tipRequestList.title"), FontService.demi(24));
        title.setForeground(ColorService.EmphasizedText);

        setLayout(new MigLayout("fillx", "20[]0", "25[]27"));
        add(title);
    }
}
