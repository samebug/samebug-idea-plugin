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
package com.samebug.clients.swing.ui.frame.welcome;

import com.samebug.clients.common.ui.frame.welcome.IWelcomeFrame;
import com.samebug.clients.common.ui.modules.MessageService;
import com.samebug.clients.swing.ui.base.frame.BasicFrame;
import com.samebug.clients.swing.ui.base.label.SamebugLabel;
import com.samebug.clients.swing.ui.base.multiline.SamebugMultilineLabel;
import com.samebug.clients.swing.ui.base.panel.SamebugPanel;
import com.samebug.clients.swing.ui.component.profile.ProfilePanel;
import com.samebug.clients.swing.ui.modules.ColorService;
import com.samebug.clients.swing.ui.modules.FontService;
import com.samebug.clients.swing.ui.modules.IconService;
import com.samebug.clients.swing.ui.modules.ListenerService;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;

public final class WelcomeFrame extends BasicFrame implements IWelcomeFrame {
    private WelcomePanel welcomePanel;

    public WelcomeFrame() {
        setLoading();
    }

    public void loadingSucceeded(Model model) {
        welcomePanel = new WelcomePanel(model);
        addMainComponent(welcomePanel);
    }

    private final class WelcomePanel extends SamebugPanel {
        private final ProfilePanel profilePanel;
        private final WelcomePanelInner welcomePanelInner;

        WelcomePanel(Model model) {
            profilePanel = new ProfilePanel(model.profilePanel);
            welcomePanelInner = new WelcomePanelInner();

            setLayout(new BorderLayout());
            add(welcomePanelInner, BorderLayout.CENTER);
            add(profilePanel, BorderLayout.SOUTH);
        }
    }

    private final class WelcomePanelInner extends SamebugPanel {
        WelcomePanelInner() {
            TitleLabel title = new TitleLabel();
            JLabel i = new JLabel(null, IconService.welcomeSnapshot, SwingConstants.LEFT);
            SamebugMultilineLabel l1 = new SamebugMultilineLabel();
            l1.setText(MessageService.message("samebug.frame.welcome.l1"));
            SamebugMultilineLabel l2 = new SamebugMultilineLabel();
            l2.setText(MessageService.message("samebug.frame.welcome.l2"));

            setLayout(new MigLayout("fillx", "20px[grow]20px", "30px[]30px[]10px[]30px[]0:push"));
            add(title, "cell 0 0, al center");
            add(l1, "cell 0 1, al left, grow x, wmin 0");
            add(i, "cell 0 2, al left, grow x, wmin 0");
            add(l2, "cell 0 3, al left, grow x, wmin 0");
        }
    }

    private final class TitleLabel extends SamebugLabel {
        {
            setText(MessageService.message("samebug.frame.welcome.title"));
            setFont(FontService.demi(24));
            setForegroundColor(ColorService.EmphasizedText);
        }
    }

    @Override
    protected Listener getListener() {
        return ListenerService.getListener(this, Listener.class);
    }
}
