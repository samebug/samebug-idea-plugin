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
import com.samebug.clients.common.ui.modules.MessageService;
import com.samebug.clients.common.ui.modules.TrackingService;
import com.samebug.clients.swing.tracking.SwingRawEvent;
import com.samebug.clients.swing.tracking.TrackingKeys;
import com.samebug.clients.swing.ui.base.label.SamebugLabel;
import com.samebug.clients.swing.ui.base.panel.TransparentPanel;
import com.samebug.clients.swing.ui.modules.ColorService;
import com.samebug.clients.swing.ui.modules.DataService;
import com.samebug.clients.swing.ui.modules.FontService;
import com.samebug.clients.swing.ui.modules.ListenerService;
import net.miginfocom.swing.MigLayout;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public final class ProfilePanel extends TransparentPanel implements IProfilePanel {
    private static final int AvatarIconSize = 26;

    private Model model;
    private AvatarIcon avatarIcon;
    private MessageLabel messages;
    private NumberLabel marks;
    private NumberLabel tips;
    private NumberLabel thanks;

    public ProfilePanel(@NotNull final Model model) {
        this.model = model;
        updateState();
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


    @Override
    @NotNull
    public Model getModel() {
        return model;
    }

    @Override
    public void setModel(@NotNull final Model model) {
        this.model = model;
        updateState();
    }

    private void updateState() {
        removeAll();

        avatarIcon = new AvatarIcon(model.avatarUrl, AvatarIconSize, model.status);
        SamebugLabel name = new SamebugLabel(model.name, FontService.demi(14));
        final JPanel glue = new TransparentPanel();
        messages = new MessageLabel(model.messages);
        marks = new NumberLabel(model.marks, MessageService.message("samebug.component.profile.marks.label"));
        tips = new NumberLabel(model.tips, MessageService.message("samebug.component.profile.tips.label"));
        thanks = new NumberLabel(model.thanks, MessageService.message("samebug.component.profile.thanks.label"));
        DataService.putData(messages, TrackingKeys.Label, messages.getText());

        setLayout(new MigLayout("fillx", "0[]8px[]0[grow]0[]19px[]19px[]19px[]0", "10px[]10px"));

        add(avatarIcon, "");
        add(name, "");
        add(glue, "");
        add(messages, "");
        add(marks, "");
        add(tips, "");
        add(thanks, "");

        repaint();
        revalidate();

        messages.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                getListener().messagesClicked();
                TrackingService.trace(SwingRawEvent.buttonClick(messages));
            }
        });
    }
}

