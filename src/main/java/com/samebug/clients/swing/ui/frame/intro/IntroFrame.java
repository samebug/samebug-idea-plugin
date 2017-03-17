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
package com.samebug.clients.swing.ui.frame.intro;

import com.samebug.clients.common.api.entities.helpRequest.HelpRequest;
import com.samebug.clients.common.api.entities.helpRequest.Requester;
import com.samebug.clients.common.ui.frame.IIntroFrame;
import com.samebug.clients.idea.components.application.IdeaSamebugPlugin;
import com.samebug.clients.swing.ui.base.frame.BasicFrame;
import com.samebug.clients.swing.ui.base.label.SamebugLabel;
import com.samebug.clients.swing.ui.modules.ListenerService;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Date;

// TODO
public final class IntroFrame extends BasicFrame implements IIntroFrame {
    public IntroFrame() {
        SamebugLabel l = new SamebugLabel("TODO intro screen");
        addMainComponent(l);
        l.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                HelpRequest h = new HelpRequest("x",
                        new Requester(1, true, "Petra", null),
                        2,
                        "ugy elakadtam...",
                        "",
                        new Date(),
                        null);
                IdeaSamebugPlugin.getInstance().notificationController.incomingHelpRequest(h);
            }
        });
    }

    @Override
    protected FrameListener getListener() {
        return ListenerService.getListener(this, Listener.class);
    }
}
