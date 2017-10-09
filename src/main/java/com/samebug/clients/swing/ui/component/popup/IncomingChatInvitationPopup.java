/*
 * Copyright 2017 Samebug, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 *    http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.samebug.clients.swing.ui.component.popup;

import com.samebug.clients.common.ui.component.popup.IIncomingChatInvitationPopup;
import com.samebug.clients.common.ui.modules.MessageService;
import com.samebug.clients.common.ui.modules.TrackingService;
import com.samebug.clients.swing.tracking.SwingRawEvent;
import com.samebug.clients.swing.tracking.TrackingKeys;
import com.samebug.clients.swing.ui.base.button.SamebugButton;
import com.samebug.clients.swing.ui.base.label.SamebugLabel;
import com.samebug.clients.swing.ui.base.panel.SamebugPanel;
import com.samebug.clients.swing.ui.component.profile.AvatarIcon;
import com.samebug.clients.swing.ui.modules.ColorService;
import com.samebug.clients.swing.ui.modules.DataService;
import com.samebug.clients.swing.ui.modules.FontService;
import com.samebug.clients.swing.ui.modules.ListenerService;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.MessageFormat;

public final class IncomingChatInvitationPopup extends SamebugPanel implements IIncomingChatInvitationPopup {
    private static final int AvatarSize = 40;

    public IncomingChatInvitationPopup(Model model) {
        final JComponent avatar = new AvatarIcon(model.avatarUrl, AvatarSize);
        final SamebugLabel title = new SamebugLabel(MessageService.message("samebug.component.chat.invitation.incoming.title", model.displayName), FontService.demi(14));
        final SamebugButton openChat = new OpenChatButton();


        setBackgroundColor(ColorService.Background);
        title.setForegroundColor(ColorService.EmphasizedText);
        openChat.setFilled(true);

        setLayout(new MigLayout("", MessageFormat.format("10px[{0}px!]8px[320px]10px", AvatarSize), "10px[]5px[]10px"));
        add(avatar, "cell 0 0, spany 2, align center top");
        add(title, "cell 1 0");
        add(openChat, "cell 1 1, align center, wmin 0");


    }

    final class OpenChatButton extends SamebugButton {
        {
            setText(MessageService.message("samebug.component.chat.invitation.incoming.open"));
            DataService.putData(this, TrackingKeys.Label, getText());
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (isEnabled()) {
                        getListener().openChat(IncomingChatInvitationPopup.this);
                        TrackingService.trace(SwingRawEvent.buttonClick(OpenChatButton.this));
                    }
                }
            });
        }
    }

    Listener getListener() {
        return ListenerService.getListener(this, IIncomingChatInvitationPopup.Listener.class);
    }
}
