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
package com.samebug.clients.swing.ui.component.helpRequest;

import com.samebug.clients.common.ui.component.helpRequest.IHelpRequest;
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

public final class HelpRequest extends RoundedBackgroundPanel implements IHelpRequest {
    private final static int AvatarSize = 40;

    public HelpRequest(Model model) {
        final SamebugLabel titleLabel = new TitleLabel();
        final AvatarIcon avatar = new AvatarIcon(model.avatarUrl, AvatarSize);
        final SamebugLabel displayName = new DisplayName(model.displayName);
        final HelpRequestBody helpRequestBody = new HelpRequestBody(model.helpRequestBody);
        final WriteTipArea writeTipArea = new WriteTipArea(MessageService.message("samebug.component.helpRequest.answer.placeholder", model.displayName));
        final ActionRow actions = new ActionRow();

        setBackgroundColor(ColorService.Tip);
        setLayout(new MigLayout("fillx", "20[40!]10[300, fill]20", "20[]20[]0[]30[]10[]20"));
        add(titleLabel, "cell 0 0, spanx 2");
        add(avatar, "cell 0 1, spany 2, top");
        add(displayName, "cell 1 1, growx");
        add(helpRequestBody, "cell 1 2, wmin 0");
        add(writeTipArea, "cell 0 3, wmin 0, spanx 2, growx");
        add(actions, "cell 0 4, spanx 2, growx");

    }

    final class TitleLabel extends SamebugLabel {
        public TitleLabel() {
            setText(MessageService.message("samebug.component.helpRequest.answer.title"));
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

    final class HelpRequestBody extends SamebugMultilineLabel {
        public HelpRequestBody(String body) {
            setText(body);
            setFont(FontService.regular(16));
            setForegroundColor(ColorService.TipText);
        }
    }

    // TODO loading animation on every button starts request
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
            // TODO postponed feature
//            final SamebugLabel why = new SamebugLabel(MessageService.message("samebug.component.helpRequest.answer.explain"));
//            why.setHorizontalAlignment(SwingConstants.RIGHT);

            setLayout(new MigLayout("fillx", "0[]0", "0[]0"));
            add(sendButton);
        }

    }

}
