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
package com.samebug.clients.swing.ui.component.tipRequest;

import com.samebug.clients.common.ui.component.tipRequest.ITipRequest;
import com.samebug.clients.swing.ui.base.button.SamebugButton;
import com.samebug.clients.swing.ui.base.label.SamebugLabel;
import com.samebug.clients.swing.ui.base.multiline.SamebugMultilineLabel;
import com.samebug.clients.swing.ui.base.panel.RoundedBackgroundPanel;
import com.samebug.clients.swing.ui.base.panel.TransparentPanel;
import com.samebug.clients.swing.ui.component.community.writeTip.WriteTipArea;
import com.samebug.clients.swing.ui.component.profile.AvatarIcon;
import com.samebug.clients.swing.ui.modules.ColorService;
import com.samebug.clients.swing.ui.modules.FontService;
import com.samebug.clients.swing.ui.modules.MessageService;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;

// TODO colors, write tip area prompt, etc
public final class TipRequest extends RoundedBackgroundPanel implements ITipRequest {
    private final static int AvatarSize = 40;

    public TipRequest(Model model) {
        final SamebugLabel titleLabel = new TitleLabel();
        final AvatarIcon avatar = new AvatarIcon(model.avatarUrl, AvatarSize);
        final SamebugLabel displayName = new DisplayName(model.displayName);
        final TipRequestBody tipRequestBody = new TipRequestBody(model.tipRequestBody);
        final WriteTipArea writeTipArea = new WriteTipArea(MessageService.message("samebug.component.tipRequest.answer.placeholder", model.displayName));
        final ActionRow actions = new ActionRow();

        setBackgroundColor(ColorService.Tip);
        setLayout(new MigLayout("fillx", "20[40!]10[300, fill]20", "20[]20[]0[]30[]10[]20"));
        add(titleLabel, "cell 0 0, spanx 2");
        add(avatar, "cell 0 1, spany 2, top");
        add(displayName, "cell 1 1, growx");
        add(tipRequestBody, "cell 1 2, wmin 0");
        add(writeTipArea, "cell 0 3, wmin 0, spanx 2, growx");
        add(actions, "cell 0 4, spanx 2, growx");

    }

    final class TitleLabel extends SamebugLabel {
        public TitleLabel() {
            setText("TIP REQUEST");
            setFont(FontService.regular(16));
            setForegroundColor(ColorService.TipText);
        }
    }

    final class DisplayName extends SamebugLabel {
        public DisplayName(String name) {
            super(name);
            setFont(FontService.demi(16));
            setForegroundColor(ColorService.TipText);
        }
    }

    final class TipRequestBody extends SamebugMultilineLabel {
        public TipRequestBody(String body) {
            setText(body);
            setFont(FontService.regular(16));
            setForegroundColor(ColorService.TipText);
        }
    }

    final class SendButton extends SamebugButton {
        public SendButton() {
            setText(MessageService.message("samebug.component.tip.write.send"));
            setFilled(true);
            setInteractionColors(ColorService.MarkInteraction);
            setBackgroundColor(ColorService.Tip);
            setFont(FontService.demi(14));
        }
    }

    final class ActionRow extends TransparentPanel {
        {
            final SamebugButton sendButton = new SendButton();
            final SamebugLabel why = new SamebugLabel("Why did I receive this message?");
            why.setHorizontalAlignment(SwingConstants.RIGHT);

            setLayout(new MigLayout("fillx", "0[]:push[]0", "0[]0"));
            add(sendButton);
            add(why);
        }

    }

}
