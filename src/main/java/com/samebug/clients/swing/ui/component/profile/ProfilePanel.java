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
package com.samebug.clients.swing.ui.component.profile;

import com.samebug.clients.common.ui.component.profile.IProfilePanel;
import com.samebug.clients.swing.ui.base.label.SamebugLabel;
import com.samebug.clients.swing.ui.base.panel.TransparentPanel;
import com.samebug.clients.swing.ui.modules.ColorService;
import com.samebug.clients.swing.ui.modules.FontService;
import com.samebug.clients.swing.ui.modules.ListenerService;
import com.samebug.clients.swing.ui.modules.MessageService;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public final class ProfilePanel extends TransparentPanel implements IProfilePanel {
    private final static int AvatarIconSize = 26;

    private final Model model;

    private final AvatarIcon avatarIcon;
    private final MessageLabel messages;
    private final NumberLabel marks;
    private final NumberLabel tips;
    private final NumberLabel thanks;

    public ProfilePanel(Model model) {
        this.model = new Model(model);

        avatarIcon = new AvatarIcon(model.avatarUrl, AvatarIconSize);
        SamebugLabel name = new SamebugLabel(model.name, FontService.demi(14));
        final JPanel glue = new TransparentPanel();
        messages = new MessageLabel(model.messages);
        marks = new NumberLabel(model.marks, MessageService.message("samebug.component.profile.marks.label"));
        tips = new NumberLabel(model.tips, MessageService.message("samebug.component.profile.tips.label"));
        thanks = new NumberLabel(model.thanks, MessageService.message("samebug.component.profile.thanks.label"));

        setLayout(new MigLayout("fillx", "0[]8[]0[grow]0[]19[]19[]19[]0", "10[]10"));

        add(avatarIcon, "");
        add(name, "");
        add(glue, "");
        add(messages, "");
        add(marks, "");
        add(tips, "");
        add(thanks, "");

        messages.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                getListener().messagesClicked();
            }
        });

    }

    @Override
    public void updateUI() {
        super.updateUI();
        // Border is set here so the color will be updated on theme change
        Color borderColor = ColorService.forCurrentTheme(ColorService.Separator);
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, borderColor),
                BorderFactory.createEmptyBorder(0, 20, 0, 20)
        ));
    }


    private Listener getListener() {
        return ListenerService.getListener(this, IProfilePanel.Listener.class);
    }

}

