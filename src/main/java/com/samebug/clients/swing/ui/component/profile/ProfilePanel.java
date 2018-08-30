/*
 * Copyright 2018 Samebug, Inc.
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
import com.samebug.clients.common.ui.modules.TrackingService;
import com.samebug.clients.swing.tracking.SwingRawEvent;
import com.samebug.clients.swing.ui.base.label.LinkLabel;
import com.samebug.clients.swing.ui.base.panel.TransparentPanel;
import com.samebug.clients.swing.ui.modules.ColorService;
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
    private LinkLabel name;

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
        name = new LinkLabel(model.name, FontService.demi(14));

        setLayout(new MigLayout("fillx", "0[]8px[grow]0", "10px[]10px"));

        add(avatarIcon, "");
        add(name, "");

        repaint();
        revalidate();

        name.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                getListener().profileClicked(model.userId);
                TrackingService.trace(SwingRawEvent.buttonClick(name));
            }
        });
    }
}
