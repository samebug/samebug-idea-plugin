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

import com.samebug.clients.common.ui.component.bugmate.IBugmateHit;
import com.samebug.clients.common.ui.modules.TextService;
import com.samebug.clients.swing.ui.base.label.SamebugLabel;
import com.samebug.clients.swing.ui.base.panel.TransparentPanel;
import com.samebug.clients.swing.ui.component.profile.AvatarIcon;
import com.samebug.clients.swing.ui.modules.ColorService;
import com.samebug.clients.swing.ui.modules.FontService;
import com.samebug.clients.swing.ui.modules.MessageService;
import net.miginfocom.swing.MigLayout;

import java.util.Date;

public final class BugmateHit extends TransparentPanel implements IBugmateHit {
    private static final int AvatarSize = 44;

    public BugmateHit(Model model) {
        final NameLabel name = new NameLabel(model.displayName);
        final TimestampLabel timestamp = new TimestampLabel(model.nSeen, model.lastSeen);
        final AvatarIcon avatar = new AvatarIcon(model.avatarUrl, AvatarSize, model.status);

        setLayout(new MigLayout("", "0[]10px[]0", "0[]0[]0"));

        add(avatar, "cell 0 0, spany 2");
        add(name, "cell 1 0");
        add(timestamp, "cell 1 1");
    }


    private final class NameLabel extends SamebugLabel {
        private NameLabel(String name) {
            setText(name);
            setForegroundColor(ColorService.EmphasizedText);
            setFont(FontService.demi(14));
        }
    }

    private final class TimestampLabel extends SamebugLabel implements com.samebug.clients.swing.ui.base.label.TimestampLabel {
        private final int nSeen;
        private final Date lastSeen;

        private TimestampLabel(int nSeen, Date lastSeen) {
            this.nSeen = nSeen;
            this.lastSeen = lastSeen;
            assert nSeen > 0 : "a bugmate should have seen the problem for which he qualifies as a bugmate at least once";
            setFont(FontService.regular(14));
            updateRelativeTimestamp();
        }

        @Override
        public void updateRelativeTimestamp() {
            setText(MessageService.message("samebug.component.bugmate.hit.occurred", nSeen, TextService.prettyTime(lastSeen)));
        }
    }
}
