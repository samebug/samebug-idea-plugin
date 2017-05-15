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

import com.samebug.clients.common.ui.component.hit.ITipHit;
import com.samebug.clients.swing.ui.base.animation.ComponentAnimation;
import com.samebug.clients.swing.ui.base.animation.ControllableAnimation;
import com.samebug.clients.swing.ui.base.animation.FadeInAnimation;
import com.samebug.clients.swing.ui.base.label.SamebugLabel;
import com.samebug.clients.swing.ui.base.panel.RoundedBackgroundPanel;
import com.samebug.clients.swing.ui.base.panel.TransparentPanel;
import com.samebug.clients.swing.ui.modules.ColorService;
import com.samebug.clients.swing.ui.modules.DataService;
import com.samebug.clients.swing.ui.modules.FontService;
import com.samebug.clients.swing.ui.modules.MessageService;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;

public final class MarkableTipHit extends RoundedBackgroundPanel implements ITipHit {

    private ComponentAnimation myAnimation;

    public MarkableTipHit(Model model) {
        setBackgroundColor(ColorService.Tip);
        DataService.putData(this, DataService.SolutionId, model.solutionId);
        SamebugLabel tipLabel = new SamebugLabel(MessageService.message("samebug.component.tipHit.title"), FontService.regular(14));
        tipLabel.setForegroundColor(ColorService.TipText);
        MessageLabel tipMessage = new MessageLabel(model.message);
        MarkButton mark = new MarkButton(model.mark);
        mark.setBackgroundColor(ColorService.Tip);
        final JPanel filler = new TransparentPanel();
        final TipAuthorPanel author = new TipAuthorPanel(model.createdBy, model.createdAt, model.createdByAvatarUrl);

        setLayout(new MigLayout("fillx", "20px[fill, 300px]20px", "18px[]13px[]15px[]20px"));

        add(tipLabel, "cell 0 0");
        add(tipMessage, "cell 0 1, wmin 0, growx");
        add(mark, "cell 0 2, align left");
        add(filler, "cell 0 2, growx");
        add(author, "cell 0 2, align right");
    }

    public ControllableAnimation fadeIn(int totalFrames) {
        myAnimation = new MyFadeInAnimation(totalFrames);
        return myAnimation;
    }

    @Override
    public void paint(Graphics g) {
        if (myAnimation == null || !myAnimation.isRunning()) super.paint(g);
        else myAnimation.paint(g);
    }

    final class MyFadeInAnimation extends FadeInAnimation {
        MyFadeInAnimation(int totalFrames) {
            super(MarkableTipHit.this, totalFrames);
        }

        @Override
        protected void doFinish() {
            MarkableTipHit.this.repaint();
        }
    }
}
