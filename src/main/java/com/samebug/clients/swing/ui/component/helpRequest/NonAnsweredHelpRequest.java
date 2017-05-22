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
import com.samebug.clients.common.ui.modules.MessageService;
import com.samebug.clients.swing.tracking.TrackingKeys;
import com.samebug.clients.swing.ui.base.animation.ComponentAnimationController;
import com.samebug.clients.swing.ui.base.animation.IAnimatedComponent;
import com.samebug.clients.swing.ui.base.animation.PaintableAnimation;
import com.samebug.clients.swing.ui.base.animation.ShrinkAwayAnimation;
import com.samebug.clients.swing.ui.base.button.SamebugButton;
import com.samebug.clients.swing.ui.base.label.SamebugLabel;
import com.samebug.clients.swing.ui.base.panel.RoundedBackgroundPanel;
import com.samebug.clients.swing.ui.base.panel.TransparentPanel;
import com.samebug.clients.swing.ui.component.community.writeTip.WriteTipArea;
import com.samebug.clients.swing.ui.component.profile.AvatarIcon;
import com.samebug.clients.swing.ui.modules.ColorService;
import com.samebug.clients.swing.ui.modules.DataService;
import com.samebug.clients.swing.ui.modules.FontService;
import com.samebug.clients.swing.ui.modules.ListenerService;
import net.miginfocom.swing.MigLayout;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public final class NonAnsweredHelpRequest extends RoundedBackgroundPanel implements IAnimatedComponent, IHelpRequest, IHelpOthersCTA {
    static final int AvatarSize = 40;

    final ActionRow actions;
    final WriteTipArea writeTipArea;
    final IHelpRequest.Model model;

    @NotNull
    private final ComponentAnimationController myAnimationController;

    public NonAnsweredHelpRequest(@NotNull IHelpRequest.Model model) {
        myAnimationController = new ComponentAnimationController(this);
        this.model = model;
        final SamebugLabel titleLabel = new HelpRequestTitleLabel();
        final AvatarIcon avatar = new AvatarIcon(model.avatarUrl, AvatarSize);
        final SamebugLabel displayName = new DisplayName(model.displayName);
        final HelpRequestBody helpRequestBody = new HelpRequestBody(model.helpRequestBody);
        writeTipArea = new WriteTipArea(MessageService.message("samebug.component.helpRequest.answer.placeholder", model.displayName));
        actions = new ActionRow();

        setBackgroundColor(ColorService.Tip);
        setLayout(new MigLayout("fillx", "20px[40px!]10px[300px, fill]20px", "20px[]20px[]0[]30px[]10px[]20px"));
        add(titleLabel, "cell 0 0, spanx 2");
        add(avatar, "cell 0 1, spany 2, top");
        add(displayName, "cell 1 1, growx");
        add(helpRequestBody, "cell 1 2, wmin 0");
        add(writeTipArea, "cell 0 3, wmin 0, spanx 2, growx");
        add(actions, "cell 0 4, spanx 2, growx");

    }

    @Override
    public void startPostTip() {
        actions.sendButton.changeToLoadingAnimation();
    }

    @Override
    public void successPostTip(@NotNull ITipHit.Model tip) {
        actions.sendButton.revertFromLoadingAnimation();
    }

    @Override
    public void failPostTipWithFormError(@Nullable final BadRequest errors) {
        actions.sendButton.revertFromLoadingAnimation();
        if (errors != null) {
            if (errors.tipBody != null) writeTipArea.setFormError(errors.tipBody);
        }
        revalidate();
        repaint();
    }

    @Override
    public void paint(Graphics g) {
        myAnimationController.paint(g);
    }

    @Override
    public void paintOriginalComponent(Graphics g) {
        super.paint(g);
    }

    public IHelpRequest.Model getModel() {
        return model;
    }

    public PaintableAnimation shrinkAway(int shrinkToSize) {
        PaintableAnimation myAnimation = new ShrinkAway(getHeight() - shrinkToSize);
        myAnimationController.prepareNewAnimation(myAnimation);
        return myAnimation;
    }

    Listener getListener() {
        return ListenerService.getListener(this, Listener.class);
    }

    final class SendButton extends SamebugButton {
        SendButton() {
            super();
            setText(MessageService.message("samebug.component.tip.write.send"));
            setFilled(true);
            setInteractionColors(ColorService.MarkInteraction);
            setBackgroundColor(ColorService.Tip);
            setFont(FontService.demi(14));
            DataService.putData(this, TrackingKeys.Label, getText());
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (isEnabled()) {
                        getListener().postTip(NonAnsweredHelpRequest.this, writeTipArea.getText());
                    }
                }
            });
        }
    }

    final class ActionRow extends TransparentPanel {
        final SendButton sendButton;

        {
            sendButton = new SendButton();
            // TODO postponed feature
//            final SamebugLabel why = new SamebugLabel(MessageService.message("samebug.component.helpRequest.answer.explain"));
//            why.setHorizontalAlignment(SwingConstants.RIGHT);

            setLayout(new MigLayout("fillx", "0[]0", "0[]0"));
            add(sendButton);
        }
    }

    private final class ShrinkAway extends ShrinkAwayAnimation {
        ShrinkAway(int shrinkPixels) {
            super(30, NonAnsweredHelpRequest.this, shrinkPixels);
        }

        @Override
        protected void doFinish() {
            revalidate();
            repaint();
        }
    }
}
