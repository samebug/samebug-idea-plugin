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
package com.samebug.clients.swing.ui.frame.tipRequestList;

import com.samebug.clients.common.ui.frame.tipRequestList.ITipRequestListFrame;
import com.samebug.clients.swing.ui.base.errorBarPane.BasicFrame;
import com.samebug.clients.swing.ui.base.label.SamebugLabel;
import com.samebug.clients.swing.ui.base.panel.SamebugPanel;
import com.samebug.clients.swing.ui.component.profile.ProfilePanel;

import java.awt.*;

public final class TipRequestListFrame extends BasicFrame implements ITipRequestListFrame {
    private TipRequests tipRequests;

    public TipRequestListFrame() {
        setLoading();
    }

    public void loadingSucceeded(Model model) {
        tipRequests = new TipRequests(model);
        addMainComponent(tipRequests);
    }

    public void setLoading() {
        addMainComponent(new SamebugLabel("loading"));
    }

    private final class TipRequests extends SamebugPanel {
        private final TipRequestListHeader header;
        private final TipRequestList list;
        private final ProfilePanel profilePanel;

        TipRequests(Model model) {
            header = new TipRequestListHeader(model.header);
            list = new TipRequestList(model.requestList);
            profilePanel = new ProfilePanel(model.profilePanel);

            setLayout(new BorderLayout());
            add(header, BorderLayout.NORTH);
            add(list, BorderLayout.CENTER);
            add(profilePanel, BorderLayout.SOUTH);
        }
    }


}
