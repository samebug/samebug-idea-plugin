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
package com.samebug.clients.swing.ui.frame.helpRequest;

import com.samebug.clients.common.ui.frame.helpRequest.IHelpRequestFrame;
import com.samebug.clients.swing.ui.base.frame.BasicFrame;
import com.samebug.clients.swing.ui.base.panel.SamebugPanel;
import com.samebug.clients.swing.ui.component.profile.ProfilePanel;
import com.samebug.clients.swing.ui.modules.ListenerService;

import javax.swing.*;
import java.awt.*;

public final class HelpRequestFrame extends BasicFrame implements IHelpRequestFrame {
    private HelpRequest helpRequest;

    public HelpRequestFrame() {
        setLoading();
    }

    @Override
    public void loadingSucceeded(Model model) {
        helpRequest = new HelpRequest(model);
        addMainComponent(helpRequest);
    }

    private final class HelpRequest extends SamebugPanel {
        private final JComponent exceptionHeader;
        private final JComponent tabs;
        private final JComponent profilePanel;

        HelpRequest(Model model) {
            exceptionHeader = new HelpRequestHeader(model.header);
            tabs = new HelpRequestTabs(model.resultTabs);
            profilePanel = new ProfilePanel(model.profilePanel);

            setLayout(new BorderLayout());
            add(exceptionHeader, BorderLayout.NORTH);
            add(tabs, BorderLayout.CENTER);
            add(profilePanel, BorderLayout.SOUTH);
        }
    }

    @Override
    protected FrameListener getListener() {
        return ListenerService.getListener(this, Listener.class);
    }
}