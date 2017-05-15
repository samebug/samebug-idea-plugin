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
package com.samebug.clients.swing.ui.component.helpRequest;

import com.samebug.clients.common.ui.component.helpRequest.IHelpRequest;
import com.samebug.clients.common.ui.component.hit.ITipHit;
import com.samebug.clients.swing.ui.base.label.SamebugLabel;
import com.samebug.clients.swing.ui.base.multiline.SamebugMultilineLabel;
import com.samebug.clients.swing.ui.base.panel.RoundedBackgroundPanel;
import com.samebug.clients.swing.ui.component.profile.AvatarIcon;
import com.samebug.clients.swing.ui.modules.ColorService;
import com.samebug.clients.swing.ui.modules.FontService;
import com.samebug.clients.swing.ui.modules.MessageService;
import net.miginfocom.swing.MigLayout;
import org.jetbrains.annotations.NotNull;

public final class AnsweredHelpRequestScreen extends RoundedBackgroundPanel {
    final HelpRequest helpRequest;
    final IHelpRequest.Model hrModel;
    final ITipHit.Model tipModel;

    public AnsweredHelpRequestScreen(@NotNull HelpRequest helpRequest, @NotNull IHelpRequest.Model hrModel, @NotNull ITipHit.Model tipModel) {
        this.helpRequest = helpRequest;
        this.hrModel = hrModel;
        this.tipModel = tipModel;
        final SamebugLabel titleLabel = new HelpRequestTitleLabel();
        final AvatarIcon avatar = new AvatarIcon(hrModel.avatarUrl, HelpRequest.AvatarSize);
        final SamebugLabel displayName = new DisplayName(hrModel.displayName);
        final HelpRequestBody helpRequestBody = new HelpRequestBody(hrModel.helpRequestBody);
        final SamebugLabel tipTitleLabel = new ResponseTitleLabel();
        final TipBody tipBody = new TipBody(tipModel.message);

        setBackgroundColor(ColorService.Tip);
        setLayout(new MigLayout("fillx", "20px[40px!]10px[300px, fill]20px", "20px[]20px[]0[]30px[]10px[]20px"));
        add(titleLabel, "cell 0 0, spanx 2");
        add(avatar, "cell 0 1, spany 2, top");
        add(displayName, "cell 1 1, growx");
        add(helpRequestBody, "cell 1 2, wmin 0");
        add(tipTitleLabel, "cell 0 3, spanx 2, growx");
        add(tipBody, "cell 0 4, wmin 0, spanx 2, growx");
    }
}

final class TipBody extends SamebugMultilineLabel {
    TipBody(String body) {
        setText(body);
        setFont(FontService.regular(16));
        setForegroundColor(ColorService.TipText);
    }
}

final class ResponseTitleLabel extends SamebugLabel {
    ResponseTitleLabel() {
        setText(MessageService.message("samebug.component.helpRequest.answer.yourTip"));
        setFont(FontService.regular(16));
        setForegroundColor(ColorService.TipText);
    }
}
