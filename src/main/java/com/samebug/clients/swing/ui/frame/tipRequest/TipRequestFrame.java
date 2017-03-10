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
package com.samebug.clients.swing.ui.frame.tipRequest;

import com.samebug.clients.common.ui.frame.tipRequest.ITipRequestFrame;
import com.samebug.clients.swing.ui.base.errorBarPane.BasicFrame;
import com.samebug.clients.swing.ui.base.label.SamebugLabel;
import com.samebug.clients.swing.ui.base.panel.SamebugPanel;
import com.samebug.clients.swing.ui.component.profile.ProfilePanel;

import javax.swing.*;
import java.awt.*;

public final class TipRequestFrame extends BasicFrame implements ITipRequestFrame {
    private TipRequest tipRequest;

    public TipRequestFrame() {
        setLoading();
    }

    @Override
    public void setLoading() {
        addMainComponent(new SamebugLabel("loading"));
    }

    @Override
    public void loadingSucceeded(Model model) {
        tipRequest = new TipRequest(model);
        addMainComponent(tipRequest);
    }

    private final class TipRequest extends SamebugPanel {
        private final JComponent exceptionHeader;
        private final JComponent tabs;
        private final JComponent profilePanel;

        TipRequest(Model model) {
            exceptionHeader = new TipRequestHeader(model.header);
            tabs = new TipRequestTabs(model.resultTabs);
            profilePanel = new ProfilePanel(model.profilePanel);

            setLayout(new BorderLayout());
            add(exceptionHeader, BorderLayout.NORTH);
            add(tabs, BorderLayout.CENTER);
            add(profilePanel, BorderLayout.SOUTH);
        }
    }


}
