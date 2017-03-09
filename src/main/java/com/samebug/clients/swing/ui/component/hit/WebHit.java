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
package com.samebug.clients.swing.ui.component.hit;

import com.samebug.clients.common.ui.component.hit.IWebHit;
import com.samebug.clients.common.ui.modules.TextService;
import com.samebug.clients.swing.ui.base.label.SamebugLabel;
import com.samebug.clients.swing.ui.base.multiline.LinkMultilineLabel;
import com.samebug.clients.swing.ui.base.panel.SamebugPanel;
import com.samebug.clients.swing.ui.base.panel.TransparentPanel;
import com.samebug.clients.swing.ui.modules.*;
import net.miginfocom.swing.MigLayout;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.MessageFormat;

public final class WebHit extends SamebugPanel implements IWebHit {
    private final Model model;

    private final MarkButton markButton;

    public WebHit(Model model) {
        this.model = new Model(model);

        DataService.putData(this, DataService.SolutionId, model.solutionId);
        markButton = new MarkButton(model.mark);
        final TitlePanel titlePanel = new TitlePanel();

        setLayout(new MigLayout("fillx", "0[300]0", "0[]16[]0"));

        add(titlePanel, "growx, cell 0 0");
        add(markButton, "cell 0 1");
    }

    private final class TitlePanel extends TransparentPanel {
        private final static int Size = 40;

        {
            final SourceIcon sourceIcon = new SourceIcon();
            final TitleLabel title = new TitleLabel();
            final SourceLabel source = new SourceLabel();

            setLayout(new MigLayout("", "0[]9[]0", "0[]0[]0"));
            add(sourceIcon, MessageFormat.format("w {0}!, h {0}!, cell 0 0, span 1 2, ay top", Size));
            add(title, MessageFormat.format("wmin 0, hmax {0}, growx, cell 1 0", Size));
            add(source, "wmin 0, growx, cell 1 1");

            title.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    getListener().urlClicked(model.url);
                }
            });
        }
    }

    private final class TitleLabel extends LinkMultilineLabel {
        {
            setFont(FontService.demi(16));
            setText(model.title);
        }

        @Override
        public Dimension getPreferredSize() {
            // TODO breaks when changing font
            if (getLineCount() <= 1) {
                return new Dimension(Integer.MAX_VALUE, 18);
            } else {
                return new Dimension(Integer.MAX_VALUE, TitlePanel.Size);
            }
        }
    }

    private final class SourceLabel extends SamebugLabel {
        {
            setFont(FontService.regular(12));
            String sourceText;
            if (model.createdBy == null) {
                sourceText = model.sourceName + " | " + String.format("%s", TextService.prettyTime(model.createdAt));
            } else {
                sourceText = model.sourceName + " by " + model.createdBy + " | " + String.format("%s", TextService.prettyTime(model.createdAt));
            }
            setText(sourceText);
        }
    }

    private final class SourceIcon extends TransparentPanel {
        private final Image sourceIcon;

        {
            sourceIcon = WebImageService.getScaled(model.sourceIconUrl, TitlePanel.Size, TitlePanel.Size);
        }

        @Override
        public void paintComponent(Graphics g) {
            Graphics2D g2 = DrawService.init(g);
            g2.drawImage(sourceIcon, 0, 0, null, null);
        }
    }

    private Listener getListener() {
        return ListenerService.getListener(this, IWebHit.Listener.class);
    }

}
