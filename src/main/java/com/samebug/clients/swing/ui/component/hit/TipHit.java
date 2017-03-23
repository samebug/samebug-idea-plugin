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
import com.samebug.clients.common.ui.modules.TextService;
import com.samebug.clients.swing.ui.base.animation.Animator;
import com.samebug.clients.swing.ui.base.animation.FadeInFadeOut;
import com.samebug.clients.swing.ui.base.label.SamebugLabel;
import com.samebug.clients.swing.ui.base.multiline.SamebugMultilineLabel;
import com.samebug.clients.swing.ui.base.panel.RoundedBackgroundPanel;
import com.samebug.clients.swing.ui.base.panel.TransparentPanel;
import com.samebug.clients.swing.ui.component.profile.AvatarIcon;
import com.samebug.clients.swing.ui.modules.ColorService;
import com.samebug.clients.swing.ui.modules.DataService;
import com.samebug.clients.swing.ui.modules.FontService;
import net.miginfocom.swing.MigLayout;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.util.Date;

public final class TipHit extends RoundedBackgroundPanel implements ITipHit {
    private final Model model;

    private final SamebugLabel tipLabel;
    private final MessageLabel tipMessage;
    private final MarkButton mark;

    private FadeInFadeOut myAnimation;

    public TipHit(Model model) {
        this.model = new Model(model);

        setBackgroundColor(ColorService.Tip);
        DataService.putData(this, DataService.SolutionId, model.solutionId);
        tipLabel = new SamebugLabel("TIP", FontService.regular(14));
        tipLabel.setForegroundColor(ColorService.TipText);
        tipMessage = new MessageLabel();
        mark = new MarkButton(model.mark);
        mark.setBackgroundColor(ColorService.Tip);
        final JPanel filler = new TransparentPanel();
        final AuthorPanel author = new AuthorPanel();

        setLayout(new MigLayout("fillx", "20[fill, 300]20", "18[]13[]15[]20"));

        add(tipLabel, "cell 0 0");
        add(tipMessage, "cell 0 1, wmin 0, growx");
        add(mark, "cell 0 2, align left");
        add(filler, "cell 0 2, growx");
        add(author, "cell 0 2, align right");
    }

    public void fadeIn() {
        myAnimation = new FadeInAnimation();
        myAnimation.resume();
    }

    @Override
    public void paint(Graphics g) {
        if (myAnimation == null) super.paint(g);
        else myAnimation.paint(g);
    }

    private final class MessageLabel extends SamebugMultilineLabel {
        {
            setText(TipHit.this.model.message);
            setForegroundColor(ColorService.TipText);
        }
    }

    private final class AuthorPanel extends TransparentPanel {
        private final static int AvatarIconSize = 26;
        private final SamebugLabel name;
        private final SamebugLabel timestamp;

        {
            final AvatarIcon authorIcon = new AvatarIcon(model.createdByAvatarUrl, AvatarIconSize);
            name = new SamebugLabel(model.createdBy, FontService.regular(12));
            name.setForegroundColor(ColorService.UnemphasizedText);
            timestamp = new TimestampLabel(model.createdAt);

            setLayout(new MigLayout("", "0[]5[]0", "0[14!]0[14!]0"));

            add(authorIcon, "cell 0 0, spany 2");
            add(name, "cell 1 0");
            add(timestamp, "cell 1 1");
        }
    }

    private final class TimestampLabel extends SamebugLabel implements com.samebug.clients.swing.ui.base.label.TimestampLabel {
        private final Date timestamp;

        private TimestampLabel(Date timestamp) {
            this.timestamp = timestamp;
            setFont(FontService.regular(12));
            setForegroundColor(ColorService.UnemphasizedText);
            updateRelativeTimestamp();
        }

        @Override
        public void updateRelativeTimestamp() {
            setText(TextService.prettyTime(timestamp));
        }
    }

    final class FadeInAnimation extends FadeInFadeOut {
        public FadeInAnimation() {
            super(TipHit.this, 1000, true);
        }

        protected void animationDone() {
            super.animationDone();
            TipHit.this.myAnimation = null;
        }
    }
}
