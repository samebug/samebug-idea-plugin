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
package com.samebug.clients.swing.ui.component.solutions.writeTip;

import com.samebug.clients.common.ui.component.solutions.IHelpOthersCTA;
import com.samebug.clients.swing.ui.component.util.button.SamebugButton;
import com.samebug.clients.swing.ui.component.util.label.LinkLabel;
import com.samebug.clients.swing.ui.component.util.label.SamebugLabel;
import com.samebug.clients.swing.ui.component.util.panel.SamebugPanel;
import com.samebug.clients.swing.ui.component.util.panel.TransparentPanel;
import com.samebug.clients.swing.ui.global.ColorService;
import com.samebug.clients.swing.ui.global.DrawService;
import com.samebug.clients.swing.ui.global.FontService;
import com.samebug.clients.swing.ui.global.ListenerService;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;

public class WriteTipScreen extends SamebugPanel {
    final SamebugLabel titleLabel;
    final WriteTipArea tipArea;
    final ActionRow actionRow;

    public WriteTipScreen(int peopleToHelp) {
        // TODO texts
        titleLabel = new SamebugLabel("WRITE TIP");
        tipArea = new WriteTipArea(peopleToHelp);
        actionRow = new ActionRow();

        setLayout(new MigLayout("fillx", "20[fill]20", "20[]20[]10[]20"));
        setBackground(ColorService.Tip);

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
            sendButton = new SamebugButton("Send tip", true);
            sendButton.setForeground(ColorService.MarkInteraction);
            sendButton.setFont(FontService.demi(14));

            cancelButton = new LinkLabel("Cancel");
            cancelButton.setForeground(ColorService.MarkInteraction);
            cancelButton.setFont(FontService.demi(14));

            setLayout(new MigLayout("", "0[]20[]:push"));
            add(sendButton);
            add(cancelButton);
        }

    }
    private IHelpOthersCTA.Listener getListener() {
        return ListenerService.getListener(this, IHelpOthersCTA.Listener.class);
    }
}
