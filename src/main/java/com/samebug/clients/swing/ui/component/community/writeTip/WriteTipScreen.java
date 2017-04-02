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

import com.google.common.collect.ImmutableMap;
import com.samebug.clients.common.api.form.CreateTip;
import com.samebug.clients.common.api.form.FieldError;
import com.samebug.clients.common.ui.component.form.ErrorCodeMismatchException;
import com.samebug.clients.common.ui.component.form.FieldNameMismatchException;
import com.samebug.clients.common.ui.component.form.FormMismatchException;
import com.samebug.clients.common.ui.component.form.IForm;
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

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public class WriteTipScreen extends RoundedBackgroundPanel implements IForm {
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

        setLayout(new MigLayout("fillx", "20[fill]20", "18[]13[]10[]20"));
        add(titleLabel, "cell 0 0");
        add(tipArea, "cell 0 1");
        add(actionRow, "cell 0 2");
    }

    public void setFormErrors(List<FieldError> errors) throws FormMismatchException {
        List<FieldError> mismatched = new ArrayList<FieldError>();
        for (FieldError f : errors) {
            try {
                if (CreateTip.BODY.equals(f.key)) tipArea.setFormError(f.code);
                else throw new FieldNameMismatchException(f.key);
            } catch (ErrorCodeMismatchException e) {
                mismatched.add(f);
            } catch (FieldNameMismatchException e) {
                mismatched.add(f);
            }
        }
        if (!mismatched.isEmpty()) throw new FormMismatchException(mismatched);
        revalidate();
        repaint();

        TrackingService.trace(Events.writeTipError(errors));
    }


    final class ActionRow extends TransparentPanel {
        final SamebugButton sendButton;
        final LinkLabel cancelButton;

        {
            sendButton = new SendTipButton();
            cancelButton = new LinkLabel(MessageService.message("samebug.component.tip.write.cancel"));
            cancelButton.setInteractionColors(ColorService.MarkInteraction);
            cancelButton.setFont(FontService.demi(14));

            setLayout(new MigLayout("", "0[]20[]:push", "0[]0"));
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
