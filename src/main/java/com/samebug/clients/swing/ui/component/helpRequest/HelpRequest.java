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

import com.samebug.clients.common.ui.component.community.IHelpOthersCTA;
import com.samebug.clients.common.ui.component.helpRequest.IHelpRequest;
import com.samebug.clients.common.ui.component.hit.ITipHit;
import com.samebug.clients.swing.ui.base.label.SamebugLabel;
import com.samebug.clients.swing.ui.base.multiline.SamebugMultilineLabel;
import com.samebug.clients.swing.ui.base.panel.RoundedBackgroundPanel;
import com.samebug.clients.swing.ui.modules.ColorService;
import com.samebug.clients.swing.ui.modules.FontService;
import com.samebug.clients.swing.ui.modules.ListenerService;
import com.samebug.clients.swing.ui.modules.MessageService;
import net.miginfocom.swing.MigLayout;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class HelpRequest extends RoundedBackgroundPanel implements IHelpRequest, IHelpOthersCTA {
    static final int AvatarSize = 40;
    private NonAnsweredHelpRequestScreen nonAnsweredSceen;
    private AnsweredHelpRequestScreen answeredScreen;

    public HelpRequest(IHelpRequest.Model model) {
        nonAnsweredSceen = new NonAnsweredHelpRequestScreen(this, model);
        setLayout(new MigLayout("fillx", "0[fill]0", "0[fill]0"));
        add(nonAnsweredSceen);
    }

    @Override
    public void startPostTip() {
        if (nonAnsweredSceen != null) nonAnsweredSceen.actions.sendButton.changeToLoadingAnimation();
    }

    @Override
    public void successPostTip(@NotNull ITipHit.Model tip) {
        if (nonAnsweredSceen != null) {
            nonAnsweredSceen.actions.sendButton.revertFromLoadingAnimation();
            answeredScreen = new AnsweredHelpRequestScreen(this, nonAnsweredSceen.model, tip);

            nonAnsweredSceen = null;
            removeAll();
            add(answeredScreen);
        }
    }

    @Override
    public void failPostTipWithFormError(@Nullable final BadRequest errors) {
        if (nonAnsweredSceen != null) {
            nonAnsweredSceen.actions.sendButton.revertFromLoadingAnimation();
            if (errors != null) {
                if (errors.tipBody != null) nonAnsweredSceen.writeTipArea.setFormError(errors.tipBody);
            }
            revalidate();
            repaint();

//        TrackingService.trace(Events.writeTipError(errors));
        }
    }

    Listener getListener() {
        return ListenerService.getListener(this, Listener.class);
    }
}

final class HelpRequestTitleLabel extends SamebugLabel {
    HelpRequestTitleLabel() {
        setText(MessageService.message("samebug.component.helpRequest.answer.title"));
        setFont(FontService.regular(16));
        setForegroundColor(ColorService.TipText);
    }
}

final class DisplayName extends SamebugLabel {
    DisplayName(String name) {
        super(name);
        setFont(FontService.demi(16));
        setForegroundColor(ColorService.TipText);
    }
}

final class HelpRequestBody extends SamebugMultilineLabel {
    HelpRequestBody(String body) {
        setText(body);
        setFont(FontService.regular(16));
        setForegroundColor(ColorService.TipText);
    }
}
