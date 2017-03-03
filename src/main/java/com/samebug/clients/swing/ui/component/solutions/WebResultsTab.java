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
package com.samebug.clients.swing.ui.component.solutions;

import com.intellij.util.messages.MessageBus;
import com.samebug.clients.common.ui.component.solutions.IHelpOthersCTA;
import com.samebug.clients.common.ui.component.solutions.IWebResultsTab;
import com.samebug.clients.swing.ui.ColorUtil;
import com.samebug.clients.swing.ui.DrawUtil;
import com.samebug.clients.swing.ui.SamebugBundle;
import com.samebug.clients.swing.ui.component.util.button.SamebugButton;
import com.samebug.clients.swing.ui.component.util.panel.SamebugPanel;
import com.samebug.clients.swing.ui.component.util.panel.TransparentPanel;
import com.samebug.clients.swing.ui.component.util.scrollPane.SamebugScrollPane;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public final class WebResultsTab extends TransparentPanel implements IWebResultsTab {
    private final Model model;
    private final IHelpOthersCTA.Model ctaModel;
    private final MessageBus messageBus;

    private final JScrollPane scrollPane;
    private final JPanel contentPanel;
    private final List<WebHit> webHits;

    public WebResultsTab(MessageBus messageBus, Model model, IHelpOthersCTA.Model ctaModel) {
        this.model = new Model(model);
        this.ctaModel = new IHelpOthersCTA.Model(ctaModel);
        this.messageBus = messageBus;

        webHits = new ArrayList<WebHit>();
        for (int i = 0; i < model.webHits.size(); i++) {
            WebHit.Model m = model.webHits.get(i);
            WebHit hit = new WebHit(messageBus, m);
            webHits.add(hit);
        }

        if (model.getHitsSize() == 0) {
            contentPanel = new EmptyContentPanel();
        } else {
            contentPanel = new ContentPanel();
        }
        scrollPane = new SamebugScrollPane();
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setViewportView(contentPanel);

        setLayout(new BorderLayout());
        add(scrollPane);
    }

    private final class EmptyContentPanel extends SamebugPanel {
        {
            final LargeWriteTipCTA cta = new LargeWriteTipCTA(messageBus, ctaModel) {
                {
                    label.setText(SamebugBundle.message("samebug.component.cta.writeTip.noWebHits.label", model.usersWaitingHelp));
                }
            };
            setLayout(new MigLayout("fillx", "20[fill]0", "0[]20"));
            add(cta);
        }
    }

    private final class ContentPanel extends SamebugPanel {
        {
            final ListPanel listPanel = new ListPanel();
            final MoreButton more = new MoreButton();

            setLayout(new MigLayout("fillx", "20[]0", "0[]20[]20"));

            add(listPanel, "cell 0 0, growx");
            add(more, "cell 0 1, al center");
        }
    }

    private final class ListPanel extends TransparentPanel {
        {
            // NOTE I intended to use BoxLayout, but somewhy the webHit did not fill the width of the panel
            setLayout(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.weightx = 1;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.gridwidth = GridBagConstraints.REMAINDER;

            // webHits is required to be initialized here (the hit views are actually added to the list)
            for (int i = 0; i < webHits.size(); i++) {
                if (i != 0) add(new Separator(), gbc);
                WebHit hit = webHits.get(i);
                add(hit, gbc);
            }
        }
    }

    private final static class Separator extends SamebugPanel {
        private static final int TopHeight = 20;
        private static final int BottomHeight = 16;

        {
            setPreferredSize(new Dimension(0, TopHeight + 1 + BottomHeight));
            setForeground(ColorUtil.Separator);
            setBackground(ColorUtil.Background);
        }

        @Override
        public void paint(Graphics g) {
            Graphics2D g2 = DrawUtil.init(g);
            g2.setColor(getBackground());
            g2.fillRect(0, 0, getWidth(), getHeight());
            g2.setColor(getForeground());
            g2.drawLine(0, TopHeight + 1, getWidth(), TopHeight + 1);
        }
    }

    private final class MoreButton extends SamebugButton {
        {
            setText(SamebugBundle.message("samebug.component.webResults.more"));
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    getListener().moreClicked();
                }
            });
        }
    }

    private Listener getListener() {
        return messageBus.syncPublisher(Listener.TOPIC);
    }
}
