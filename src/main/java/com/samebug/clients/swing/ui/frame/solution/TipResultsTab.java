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
package com.samebug.clients.swing.ui.frame.solution;

import com.samebug.clients.common.ui.component.bugmate.IBugmateList;
import com.samebug.clients.common.ui.component.community.IAskForHelp;
import com.samebug.clients.common.ui.component.community.IHelpOthersCTA;
import com.samebug.clients.common.ui.component.helpRequest.IMyHelpRequest;
import com.samebug.clients.common.ui.component.hit.ITipHit;
import com.samebug.clients.common.ui.frame.solution.ITipResultsTab;
import com.samebug.clients.swing.ui.base.animation.*;
import com.samebug.clients.swing.ui.base.panel.SamebugPanel;
import com.samebug.clients.swing.ui.base.panel.TransparentPanel;
import com.samebug.clients.swing.ui.base.scrollPane.SamebugScrollPane;
import com.samebug.clients.swing.ui.component.bugmate.BugmateList;
import com.samebug.clients.swing.ui.component.bugmate.RequestHelp;
import com.samebug.clients.swing.ui.component.bugmate.RevokeHelpRequest;
import com.samebug.clients.swing.ui.component.community.writeTip.WriteTip;
import com.samebug.clients.swing.ui.component.hit.TipHit;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public final class TipResultsTab extends TransparentPanel implements ITipResultsTab {
    private final ContentPanel contentPanel;
    private final List<TipHit> tipHits;

    private ComponentAnimation myAnimation;

    public TipResultsTab(Model model, IHelpOthersCTA.Model ctaModel) {
        tipHits = new ArrayList<TipHit>();
        for (int i = 0; i < model.tipHits.size(); i++) {
            TipHit.Model m = model.tipHits.get(i);
            TipHit hit = new TipHit(m);
            tipHits.add(hit);
        }
        contentPanel = new ContentPanel(ctaModel, model.bugmateList, model.myHelpRequest, model.askForHelp);
        contentPanel.setContent(tipHits);
        JScrollPane scrollPane = new SamebugScrollPane();
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
        return new MyFadeOutAnimation(totalFrames);
    }

    public ControllableAnimation fadeIn(int totalFrames) {
        return new MyFadeInAnimation(totalFrames);
    }

    public ControllableAnimation animatedAddTip(final ITipHit.Model model, final int tipFloatInFrames) {
        final TipHit newTip = new TipHit(model);
        return contentPanel.addTip(newTip, tipHits, tipFloatInFrames);
    }

    private static final class ContentPanel extends SamebugPanel {
        final ListPanel listPanel;
        final WriteTip writeTip;
        final WriteTip cta;
        final BugmateList bugmateList;
        final IBugmateList.Model bugmateListModel;
        final JComponent helpRequest;

        private ComponentAnimation contentPanelAnimation;

        ContentPanel(IHelpOthersCTA.Model ctaModel, IBugmateList.Model bugmateListModel, IMyHelpRequest.Model myHelpRequestModel, IAskForHelp.Model askForHelpModel) {
            this.bugmateListModel = bugmateListModel;
            listPanel = new ListPanel();
            writeTip = new WriteTip(ctaModel, WriteTip.CTA_TYPE.SMALL);
            cta = new WriteTip(ctaModel, WriteTip.CTA_TYPE.LARGE_FOR_TIP_HITS);
            bugmateList = new BugmateList(bugmateListModel);
            if (myHelpRequestModel != null) {
                helpRequest = new RevokeHelpRequest(myHelpRequestModel);
            } else {
                helpRequest = new RequestHelp(askForHelpModel);
            }
        }

        void setContent(List<TipHit> tipHits) {
            removeAll();
            listPanel.setContent(tipHits);
            if (tipHits.size() > 0 && bugmateListModel.bugmateHits.size() > 0) {
                setLayout(new MigLayout("fillx", "20[fill]0", "0[]20[]20[]10[]20"));
                add(listPanel, "cell 0 0");
                add(writeTip, "cell 0 1");
                add(bugmateList, "cell 0 2");
                add(helpRequest, "cell 0 3, align center, growx");
            } else if (tipHits.size() == 0 && bugmateListModel.bugmateHits.size() > 0) {
                setLayout(new MigLayout("fillx", "20[fill]0", "0[]20[]10[]20"));
                add(cta, "cell 0 0");
                add(bugmateList, "cell 0 1");
                add(helpRequest, "cell 0 2, align center, growx");
            } else if (tipHits.size() > 0 && bugmateListModel.bugmateHits.size() == 0) {
                setLayout(new MigLayout("fillx", "20[fill]0", "0[]20[]10[]20"));
                add(listPanel, "cell 0 0");
                add(writeTip, "cell 0 1");
                add(helpRequest, "cell 0 2, align center, growx");
            } else {
                setLayout(new MigLayout("fillx", "20[fill]0", "0[]10[]20"));
                add(cta, "cell 0 0");
                add(helpRequest, "cell 0 1, align center, growx");
            }
            validate();
        }

        void setZeroFromOneTipContent() {
            removeAll();
            if (bugmateListModel.bugmateHits.size() > 0) {
                setLayout(new MigLayout("fillx", "20[fill]0", "0[]20[]10[]20"));
                add(writeTip, "cell 0 0");
                add(bugmateList, "cell 0 1");
                add(helpRequest, "cell 0 2, align center, growx");
            } else {
                setLayout(new MigLayout("fillx", "20[fill]0", "0[]10[]20"));
                add(writeTip, "cell 0 0");
                add(helpRequest, "cell 0 1, align center, growx");
            }
            validate();
        }

        ControllableAnimation addTip(final TipHit newTip, final List<TipHit> tipHits, int tipFloatInFrames) {
            if (contentPanelAnimation != null) contentPanelAnimation.forceFinish();

            contentPanelAnimation = new GrowFromTop(tipFloatInFrames, newTip.getPreferredSize().height);
            contentPanelAnimation.runBeforeStart(new Runnable() {
                @Override
                public void run() {
                    tipHits.add(0, newTip);
                    setContent(tipHits);
                }
            });
            ControllableAnimation tipFadeInAnimation = newTip.fadeIn(tipFloatInFrames);
            return contentPanelAnimation.with(tipFadeInAnimation);
        }

        @Override
        public void paint(Graphics g) {
            if (contentPanelAnimation == null || !contentPanelAnimation.isRunning()) super.paint(g);
            else contentPanelAnimation.paint(g);
        }

        private final class GrowFromTop extends DynamicallyUpdatedGrowFromTopAnimation {

            GrowFromTop(int tipFloatInFrames, int growPixels) {
                super(tipFloatInFrames, ContentPanel.this, growPixels);
                assert myGrownSize.height >= growPixels : "Cannot grow " + growPixels + " pixels because its final size is less than that";
            }

            @Override
            protected void doFinish() {
                revalidate();
                repaint();
            }
        }
    }

    private static final class ListPanel extends SamebugPanel {
        ListPanel() {
            setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        }

        void setContent(List<TipHit> tipHits) {
            removeAll();
            for (int i = 0; i < tipHits.size(); i++) {
                if (i == 0) add(Box.createRigidArea(new Dimension(0, 10)));
                else add(Box.createRigidArea(new Dimension(0, 20)));
                TipHit hit = tipHits.get(i);
                add(hit);
            }
        }
    }

    private final class MyFadeInAnimation extends FadeInAnimation {

        public MyFadeInAnimation(int totalFrames) {
            super(TipResultsTab.this, totalFrames);
            runBeforeStart(new Runnable() {
                @Override
                public void run() {
                    if (tipHits.isEmpty()) {
                        contentPanel.setZeroFromOneTipContent();
                    }
                    if (myAnimation != null) myAnimation.forceFinish();
                    myAnimation = MyFadeInAnimation.this;
                }
            });
        }

        @Override
        protected void doFinish() {
            TipResultsTab.this.repaint();
        }
    }

    private final class MyFadeOutAnimation extends FadeOutAnimation {

        public MyFadeOutAnimation(int totalFrames) {
            super(TipResultsTab.this, totalFrames);
            runBeforeStart(new Runnable() {
                @Override
                public void run() {
                    if (myAnimation != null) myAnimation.forceFinish();
                    myAnimation = MyFadeOutAnimation.this;
                }
            });
        }

        @Override
        protected void doFinish() {
            TipResultsTab.this.repaint();
        }
    }
}
