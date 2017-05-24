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
package com.samebug.clients.swing.ui.frame.helpRequest;

import com.samebug.clients.common.tracking.Funnels;
import com.samebug.clients.common.tracking.PageTabs;
import com.samebug.clients.common.ui.component.community.IHelpOthersCTA;
import com.samebug.clients.common.ui.frame.solution.IWebResultsTab;
import com.samebug.clients.common.ui.modules.MessageService;
import com.samebug.clients.common.ui.modules.TrackingService;
import com.samebug.clients.swing.tracking.SwingRawEvent;
import com.samebug.clients.swing.tracking.TrackingKeys;
import com.samebug.clients.swing.ui.base.animation.ControllableAnimation;
import com.samebug.clients.swing.ui.base.animation.FadeOutAnimation;
import com.samebug.clients.swing.ui.base.animation.PaintableAnimation;
import com.samebug.clients.swing.ui.base.button.SamebugButton;
import com.samebug.clients.swing.ui.base.panel.SamebugPanel;
import com.samebug.clients.swing.ui.base.panel.TransparentPanel;
import com.samebug.clients.swing.ui.base.scrollPane.SamebugScrollPane;
import com.samebug.clients.swing.ui.component.community.writeTip.WriteTip;
import com.samebug.clients.swing.ui.component.hit.NonMarkableWebHit;
import com.samebug.clients.swing.ui.modules.ColorService;
import com.samebug.clients.swing.ui.modules.DataService;
import com.samebug.clients.swing.ui.modules.DrawService;
import com.samebug.clients.swing.ui.modules.ListenerService;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public final class WebResultsTab extends TransparentPanel implements IWebResultsTab {
    private final IHelpOthersCTA.Model ctaModel;

    private final JScrollPane scrollPane;
    private final JPanel contentPanel;
    private final List<NonMarkableWebHit> webHits;

    private PaintableAnimation myAnimation;

    public WebResultsTab(Model model, IHelpOthersCTA.Model ctaModel) {
        DataService.putData(this, TrackingKeys.PageTab, PageTabs.HelpRequest.ExternalWebHits);
        this.ctaModel = new IHelpOthersCTA.Model(ctaModel);

        webHits = new ArrayList<NonMarkableWebHit>();
        for (int i = 0; i < model.webHits.size(); i++) {
            NonMarkableWebHit.Model m = model.webHits.get(i);
            NonMarkableWebHit hit = new NonMarkableWebHit(m);
            DataService.putData(hit, TrackingKeys.SolutionTransaction, Funnels.newTransactionId());
            webHits.add(hit);
        }

        if (model.webHits.size() == 0) {
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

    @Override
    public void paint(Graphics g) {
        if (myAnimation == null || !myAnimation.isRunning()) super.paint(g);
        else myAnimation.paint(g);
    }

    public ControllableAnimation fadeOut(int totalFrames) {
        if (myAnimation != null) myAnimation.forceFinish();
        myAnimation = new MyFadeOut(totalFrames);
        return myAnimation;
    }

    private final class EmptyContentPanel extends SamebugPanel {
        {
            final WriteTip cta = new WriteTip(ctaModel, WriteTip.CTA_TYPE.LARGE_FOR_WEB_HITS);
            setLayout(new MigLayout("fillx", "20px[fill]0", "0[]20px"));
            add(cta);
        }
    }

    private final class ContentPanel extends SamebugPanel {
        {
            final ListPanel listPanel = new ListPanel();
            final MoreButton more = new MoreButton();

            setLayout(new MigLayout("fillx", "20px[]0", "0[]20px[]20px"));

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
                NonMarkableWebHit hit = webHits.get(i);
                DataService.putData(hit, TrackingKeys.SolutionHitIndex, i);
                add(hit, gbc);
            }
        }
    }

    private static final class Separator extends SamebugPanel {
        private static final int TopHeight = 20;
        private static final int BottomHeight = 16;

        {
            setPreferredSize(new Dimension(0, TopHeight + 1 + BottomHeight));
            setForegroundColor(ColorService.Separator);
            setBackgroundColor(ColorService.Background);
        }

        @Override
        public void paint(Graphics g) {
            Graphics2D g2 = DrawService.init(g);
            g2.setColor(getBackground());
            g2.fillRect(0, 0, getWidth(), getHeight());
            g2.setColor(getForeground());
            g2.drawLine(0, TopHeight + 1, getWidth(), TopHeight + 1);
        }
    }

    private final class MoreButton extends SamebugButton {
        {
            setText(MessageService.message("samebug.component.webResults.more"));
            DataService.putData(this, TrackingKeys.Label, getText());
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    getListener().moreClicked();
                    TrackingService.trace(SwingRawEvent.buttonClick(MoreButton.this));
                }
            });
        }
    }

    private Listener getListener() {
        return ListenerService.getListener(this, Listener.class);
    }

    private final class MyFadeOut extends FadeOutAnimation {

        MyFadeOut(int totalFrames) {
            super(WebResultsTab.this, totalFrames);
        }

        @Override
        protected void doFinish() {
            WebResultsTab.this.repaint();
        }
    }
}
