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
package com.samebug.clients.swing.ui.component.hit;

import com.samebug.clients.swing.ui.base.label.SamebugLabel;
import com.samebug.clients.swing.ui.base.panel.TransparentPanel;
import com.samebug.clients.swing.ui.component.profile.AvatarIcon;
import com.samebug.clients.swing.ui.modules.ColorService;
import com.samebug.clients.swing.ui.modules.FontService;
import net.miginfocom.swing.MigLayout;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.URL;
import java.util.Date;

final class TipAuthorPanel extends TransparentPanel {
    private static final int AvatarIconSize = 26;

    TipAuthorPanel(@NotNull String displayName, @NotNull Date createdAt, @Nullable URL avatarUrl) {
        final AvatarIcon authorIcon = new AvatarIcon(avatarUrl, AvatarIconSize);
        final SamebugLabel name = new SamebugLabel(displayName, FontService.regular(12));
        name.setForegroundColor(ColorService.UnemphasizedText);
        final SamebugLabel timestamp = new TipTimestampLabel(createdAt);

        setLayout(new MigLayout("", "0[]5px[]0", "0[14px!]0[14px!]0"));

        add(authorIcon, "cell 0 0, spany 2");
        add(name, "cell 1 0");
        add(timestamp, "cell 1 1");
    }
}
