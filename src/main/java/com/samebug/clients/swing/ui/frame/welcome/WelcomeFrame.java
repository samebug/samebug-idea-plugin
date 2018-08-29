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
import com.samebug.clients.swing.ui.base.frame.BasicFrame;
import com.samebug.clients.swing.ui.base.label.SamebugLabel;
import com.samebug.clients.swing.ui.base.multiline.SamebugMultilineLabel;
import com.samebug.clients.swing.ui.base.panel.SamebugPanel;
import com.samebug.clients.swing.ui.component.profile.ProfilePanel;
import com.samebug.clients.swing.ui.modules.ListenerService;

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
            SamebugMultilineLabel l = new SamebugMultilineLabel();
            add(new SamebugLabel("Samebug plugin is active!"));
            l.setText("You will find the [bug] icon near stack traces in the console. Click on it to open Samebug knowledge base to find or share help on that stack trace.");
            add(l);
        }
    }

    @Override
    protected Listener getListener() {
        return ListenerService.getListener(this, Listener.class);
    }
}
