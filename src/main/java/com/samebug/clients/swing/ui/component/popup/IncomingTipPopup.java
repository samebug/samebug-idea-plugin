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
package com.samebug.clients.swing.ui.component.popup;

import com.samebug.clients.common.ui.component.popup.IIncomingTipPopup;
import com.samebug.clients.swing.ui.base.label.SamebugLabel;
import com.samebug.clients.swing.ui.base.multiline.SamebugMultilineLabel;
import com.samebug.clients.swing.ui.base.panel.SamebugPanel;
import com.samebug.clients.swing.ui.component.profile.AvatarIcon;
import com.samebug.clients.swing.ui.modules.ColorService;
import com.samebug.clients.swing.ui.modules.FontService;
import com.samebug.clients.swing.ui.modules.MessageService;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.text.MessageFormat;

public final class IncomingTipPopup extends SamebugPanel implements IIncomingTipPopup {
    private final static int AvatarSize = 40;

    public IncomingTipPopup(Model model) {
        final JComponent avatar = new AvatarIcon(model.avatarUrl, AvatarSize);
        final SamebugLabel title = new SamebugLabel(MessageService.message("samebug.component.tip.incoming.title", model.displayName), FontService.demi(14));
        final TipBody body = new TipBody(model.tipBody);

        setBackgroundColor(ColorService.Tip);
        title.setForegroundColor(ColorService.EmphasizedText);
        body.setForegroundColor(ColorService.EmphasizedText);

        setLayout(new MigLayout("", MessageFormat.format("10px[{0}px!]8px[320px]10px", AvatarSize), "10px[]5px[]10px"));
        add(avatar, "cell 0 0, spany 2, align center top");
        add(title, "cell 1 0");
        add(body, "cell 1 1, growx, wmin 0");
    }

    final class TipBody extends SamebugMultilineLabel {
        public TipBody(String body) {
            setText(body);
            setFont(FontService.regular(14));
        }
    }
}
