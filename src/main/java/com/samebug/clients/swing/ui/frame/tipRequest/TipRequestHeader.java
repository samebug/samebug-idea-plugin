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

import com.samebug.clients.common.ui.frame.tipRequest.ITipRequestHeader;
import com.samebug.clients.swing.ui.base.label.SamebugLabel;
import com.samebug.clients.swing.ui.base.multiline.SamebugMultilineLabel;
import com.samebug.clients.swing.ui.component.profile.AvatarIcon;
import com.samebug.clients.swing.ui.modules.ColorService;
import com.samebug.clients.swing.ui.modules.FontService;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.net.URL;
import java.text.MessageFormat;

public final class TipRequestHeader extends JComponent implements ITipRequestHeader {
    private final static int AvatarSize = 26;

    public TipRequestHeader(Model model) {
        final HeaderTextLabel header = new HeaderTextLabel(model.title);
        final AuthorRow authorRow = new AuthorRow(model.displayName, model.avatarUrl);

        setLayout(new MigLayout("fillx, filly", "20[]20", "30[]0[]30"));
        add(header, "cell 0 0, wmin 0, hmax 56");
        add(authorRow, "cell 0 1");
    }


    final class HeaderTextLabel extends SamebugMultilineLabel {
        public HeaderTextLabel(String text) {
            setFont(FontService.demi(24));
            setForegroundColor(ColorService.EmphasizedText);
            setText(text);
        }

        @Override
        public Dimension getPreferredSize() {
            if (getLineCount() <= 1) {
                return new Dimension(Integer.MAX_VALUE, 24 + 2);
            } else {
                return new Dimension(Integer.MAX_VALUE, 24 * 2 + 8);
            }
        }
    }

    final class AuthorRow extends JComponent {
        public AuthorRow(String displayName, URL avatarUrl) {
            final SamebugLabel by = new SamebugLabel("by", FontService.demi(14));
            final AvatarIcon avatar = new AvatarIcon(avatarUrl, AvatarSize);
            final SamebugLabel name = new SamebugLabel(displayName, FontService.demi(14));

            setLayout(new MigLayout("", MessageFormat.format("0[]8[{0}!]8[]0", AvatarSize)));
            add(by, "cell 0 0");
            add(avatar, "cell 1 0");
            add(name, "cell 2 0");
        }
    }

}
