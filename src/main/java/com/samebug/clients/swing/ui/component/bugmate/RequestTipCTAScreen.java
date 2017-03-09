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
package com.samebug.clients.swing.ui.component.bugmate;

import com.samebug.clients.swing.ui.base.button.SamebugButton;
import com.samebug.clients.swing.ui.base.panel.TransparentPanel;
import com.samebug.clients.swing.ui.modules.MessageService;
import net.miginfocom.swing.MigLayout;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public final class RequestTipCTAScreen extends TransparentPanel {
    final BugmateList bugmateList;

    public RequestTipCTAScreen(BugmateList bugmateList) {
        this.bugmateList = bugmateList;

        final AskButton ask = new AskButton();

        setLayout(new MigLayout("", ":push[]:push", ""));
        add(ask);
    }

    final class AskButton extends SamebugButton {
        {
            setText(MessageService.message("samebug.component.bugmate.list.ask"));
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    bugmateList.requestTip.changeToOpenState();
                }
            });

        }
    }
}