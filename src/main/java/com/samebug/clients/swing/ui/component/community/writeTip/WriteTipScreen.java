/**
 * Copyright 2017 Samebug, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.samebug.clients.swing.ui.component.community.writeTip;

import com.samebug.clients.swing.ui.base.button.SamebugButton;
import com.samebug.clients.swing.ui.base.label.LinkLabel;
import com.samebug.clients.swing.ui.base.label.SamebugLabel;
import com.samebug.clients.swing.ui.base.panel.SamebugPanel;
import com.samebug.clients.swing.ui.base.panel.TransparentPanel;
import com.samebug.clients.swing.ui.modules.ColorService;
import com.samebug.clients.swing.ui.modules.DrawService;
import com.samebug.clients.swing.ui.modules.FontService;
import com.samebug.clients.swing.ui.modules.MessageService;
import net.miginfocom.swing.MigLayout;

import java.awt.*;

public class WriteTipScreen extends SamebugPanel {
    final SamebugLabel titleLabel;
    final WriteTipArea tipArea;
    final ActionRow actionRow;

    public WriteTipScreen(int peopleToHelp) {
        setOpaque(false);
        setBackground(ColorService.Tip);

        titleLabel = new SamebugLabel(MessageService.message("samebug.component.tip.write.title"), FontService.regular(14));
        titleLabel.setForeground(ColorService.TipText);
        tipArea = new WriteTipArea(peopleToHelp);
        actionRow = new ActionRow();

        setLayout(new MigLayout("fillx", "20[fill]20", "18[]13[]10[]20"));
        add(titleLabel, "cell 0 0");
        add(tipArea, "cell 0 1");
        add(actionRow, "cell 0 2");
    }

    @Override
    public void paintBorder(Graphics g) {
        Graphics2D g2 = DrawService.init(g);

        g2.setColor(getBackground());
        g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, DrawService.RoundingDiameter, DrawService.RoundingDiameter);
    }

    final class ActionRow extends TransparentPanel {
        final SamebugButton sendButton;
        final LinkLabel cancelButton;

        {
            sendButton = new SamebugButton(MessageService.message("samebug.component.tip.write.send"), true);
            sendButton.setInteractionColors(ColorService.MarkInteraction);
            sendButton.setBackgroundColors(ColorService.Tip);
            sendButton.setFont(FontService.demi(14));

            cancelButton = new LinkLabel(MessageService.message("samebug.component.tip.write.cancel"));
            cancelButton.setInteractionColors(ColorService.MarkInteraction);
            cancelButton.setFont(FontService.demi(14));

            setLayout(new MigLayout("", "0[]20[]:push", "0[]0"));
            add(sendButton);
            add(cancelButton);
        }

    }
}
