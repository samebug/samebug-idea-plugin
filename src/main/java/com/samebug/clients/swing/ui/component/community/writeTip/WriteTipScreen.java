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
package com.samebug.clients.swing.ui.component.community.writeTip;

import com.samebug.clients.common.ui.component.community.IHelpOthersCTA;
import com.samebug.clients.idea.tracking.Events;
import com.samebug.clients.swing.ui.base.button.SamebugButton;
import com.samebug.clients.swing.ui.base.label.LinkLabel;
import com.samebug.clients.swing.ui.base.label.SamebugLabel;
import com.samebug.clients.swing.ui.base.panel.RoundedBackgroundPanel;
import com.samebug.clients.swing.ui.base.panel.TransparentPanel;
import com.samebug.clients.swing.ui.modules.ColorService;
import com.samebug.clients.swing.ui.modules.FontService;
import com.samebug.clients.swing.ui.modules.MessageService;
import com.samebug.clients.swing.ui.modules.TrackingService;
import net.miginfocom.swing.MigLayout;
import org.jetbrains.annotations.NotNull;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class WriteTipScreen extends RoundedBackgroundPanel {
    final WriteTip writeTip;
    final SamebugLabel titleLabel;
    final WriteTipArea tipArea;
    final ActionRow actionRow;

    WriteTipScreen(WriteTip writeTip) {
        this.writeTip = writeTip;
        setBackgroundColor(ColorService.Tip);

        titleLabel = new SamebugLabel(MessageService.message("samebug.component.tip.write.title"), FontService.regular(14));
        titleLabel.setForegroundColor(ColorService.TipText);
        tipArea = new WriteTipArea(MessageService.message("samebug.component.tip.write.placeholder", writeTip.model.usersWaitingHelp));
        actionRow = new ActionRow();

        setLayout(new MigLayout("fillx", "20px[fill]20px", "18px[]13px[]10px[]20px"));
        add(titleLabel, "cell 0 0");
        add(tipArea, "cell 0 1");
        add(actionRow, "cell 0 2");
    }

    public void setFormErrors(@NotNull final IHelpOthersCTA.BadRequest errors) {
        if (errors.tipBody != null) tipArea.setFormError(errors.tipBody);
        revalidate();
        repaint();

//        TrackingService.trace(Events.writeTipError(errors));
    }


    final class ActionRow extends TransparentPanel {
        final SamebugButton sendButton;
        final LinkLabel cancelButton;

        {
            sendButton = new SendTipButton();
            cancelButton = new LinkLabel(MessageService.message("samebug.component.tip.write.cancel"));
            cancelButton.setInteractionColors(ColorService.MarkInteraction);
            cancelButton.setFont(FontService.demi(14));

            setLayout(new MigLayout("", "0[]20px[]:push", "0[]0"));
            add(sendButton);
            add(cancelButton);

            cancelButton.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    writeTip.changeToClosedState();
                    TrackingService.trace(Events.writeTipCancel());
                }
            });


        }

    }

    final class SendTipButton extends SamebugButton {
        public SendTipButton() {
            super(MessageService.message("samebug.component.tip.write.send"), true);
            setInteractionColors(ColorService.MarkInteraction);
            setBackgroundColor(ColorService.Tip);
            setFont(FontService.demi(14));

            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (isEnabled()) {
                        writeTip.getListener().postTip(writeTip, tipArea.getText());
                        TrackingService.trace(Events.writeTipSend());
                    }
                }
            });
        }
    }
}
