package com.samebug.clients.swing.ui.component.popup;

import com.samebug.clients.common.ui.component.popup.IIncomingChatInvitationPopup;
import com.samebug.clients.swing.ui.TestDialog;
import org.junit.Test;

import java.awt.*;

public class ChatInvitationPopupTest extends TestDialog {
    @Test
    public void popup() {
        waitToInitializeUI("chatInvitationPopup/1.json").showDialog();
    }

    public void initializeUI(String resourceJson) throws Exception {
        final IIncomingChatInvitationPopup.Model model = readJson(resourceJson, IIncomingChatInvitationPopup.Model.class);
        setPreferredSize(new Dimension(320, 180));

        IncomingChatInvitationPopup f = new IncomingChatInvitationPopup(model);
        setContentPane(f);
    }
}
