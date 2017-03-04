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
package com.samebug.clients.swing.ui.component.solutions;

import com.intellij.util.messages.MessageBus;
import com.samebug.clients.common.ui.TextUtil;
import com.samebug.clients.common.ui.component.solutions.IBugmateHit;
import com.samebug.clients.swing.ui.global.FontService;
import com.samebug.clients.swing.ui.global.MessageService;
import com.samebug.clients.swing.ui.component.util.AvatarIcon;
import com.samebug.clients.swing.ui.component.util.label.SamebugLabel;
import com.samebug.clients.swing.ui.component.util.panel.TransparentPanel;
import net.miginfocom.swing.MigLayout;

public final class BugmateHit extends TransparentPanel implements IBugmateHit {
    private final static int AvatarSize = 44;

    private final Model model;
    private final MessageBus messageBus;

    public BugmateHit(MessageBus messageBus, Model model) {
        this.model = new Model(model);
        this.messageBus = messageBus;

        final NameLabel name = new NameLabel();
        final TimestampLabel timestamp = new TimestampLabel();
        final AvatarIcon avatar = new AvatarIcon(model.avatarUrl, AvatarSize);

        setLayout(new MigLayout("", "0[]10[]0", "0[]0[]0"));

        add(avatar, "cell 0 0, spany 2");
        add(name, "cell 1 0");
        add(timestamp, "cell 1 1");
    }


    private final class NameLabel extends SamebugLabel {
        {
            setText(model.displayName);
            setFont(FontService.demi(14));
        }
    }

    private final class TimestampLabel extends SamebugLabel {
        {
            setText(MessageService.message("samebug.component.bugmate.hit.occurred", model.nSeen, TextUtil.prettyTime(model.lastSeen)));
            setFont(FontService.regular(14));
        }
    }
}
