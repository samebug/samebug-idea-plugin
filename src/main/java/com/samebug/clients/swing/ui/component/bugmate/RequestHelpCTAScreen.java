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
package com.samebug.clients.swing.ui.component.bugmate;

import com.samebug.clients.idea.tracking.Events;
import com.samebug.clients.swing.ui.base.button.SamebugButton;
import com.samebug.clients.swing.ui.base.panel.TransparentPanel;
import com.samebug.clients.swing.ui.modules.MessageService;
import com.samebug.clients.swing.ui.modules.TrackingService;
import net.miginfocom.swing.MigLayout;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public final class RequestHelpCTAScreen extends TransparentPanel {
    final RequestHelp requestHelp;

    public RequestHelpCTAScreen(RequestHelp requestHelp) {
        this.requestHelp = requestHelp;

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
                    if (isEnabled()) {
                        requestHelp.changeToOpenState();
                        TrackingService.trace(Events.helpRequestOpen());
                    }
                }
            });
        }
    }
}
