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
import com.samebug.clients.swing.ui.base.panel.SamebugPanel;
import com.samebug.clients.swing.ui.component.profile.ProfilePanel;
import com.samebug.clients.swing.ui.modules.ListenerService;

import java.awt.*;

public final class WelcomeFrame extends BasicFrame implements IWelcomeFrame {
    private HelpRequests helpRequests;

    public WelcomeFrame() {
        setLoading();
    }

    public void loadingSucceeded(Model model) {
        helpRequests = new HelpRequests(model);
        addMainComponent(helpRequests);
    }

    // TODO @poroszd rename, redesign
    private final class HelpRequests extends SamebugPanel {
        private final ProfilePanel profilePanel;

        HelpRequests(Model model) {
            profilePanel = new ProfilePanel(model.profilePanel);

            setLayout(new BorderLayout());
            add(profilePanel, BorderLayout.SOUTH);
        }
    }

    @Override
    protected Listener getListener() {
        return ListenerService.getListener(this, Listener.class);
    }
}
