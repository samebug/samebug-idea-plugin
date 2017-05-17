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
import com.samebug.clients.swing.ui.component.hit.MarkableTipHit;
import net.miginfocom.swing.MigLayout;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public final class TipResultsTab extends TransparentPanel implements IAnimatedComponent, ITipResultsTab {
    private final ContentPanel contentPanel;
    private final List<MarkableTipHit> tipHits;

    @NotNull
    private final ComponentAnimationController myAnimationController;

    public TipResultsTab(Model model, IHelpOthersCTA.Model ctaModel) {
        myAnimationController = new ComponentAnimationController(this);
        tipHits = new ArrayList<MarkableTipHit>();
        for (int i = 0; i < model.tipHits.size(); i++) {
            MarkableTipHit.Model m = model.tipHits.get(i);
            MarkableTipHit hit = new MarkableTipHit(m);
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
        myAnimationController.paint(g);
    }

    @Override
    public void paintOriginalComponent(Graphics g) {
        super.paint(g);
    }

    public ControllableAnimation fadeOut(int frames) {
        final PaintableAnimation myAnimation = new MyFadeOutAnimation(frames);
        // TODO this is still not good, AnimationController cannot handle multiple pending animations
        myAnimation.runBeforeStart(new Runnable() {
            @Override
            public void run() {
                myAnimationController.prepareNewAnimation(myAnimation);
            }
        });
        return myAnimation;
    }

    public ControllableAnimation fadeIn(int frames) {
        final PaintableAnimation myAnimation = new MyFadeInAnimation(frames);
        myAnimation.runBeforeStart(new Runnable() {
            @Override
            public void run() {
                myAnimationController.prepareNewAnimation(myAnimation);
            }
        });
        return myAnimation;
    }

    public ControllableAnimation animatedAddTip(final ITipHit.Model model) {
        final MarkableTipHit newTip = new MarkableTipHit(model);
        return contentPanel.addTip(newTip, tipHits);
    }

    private static final class ContentPanel extends SamebugPanel implements IAnimatedComponent {
        final ListPanel listPanel;
        final WriteTip writeTip;
        final WriteTip cta;
        final BugmateList bugmateList;
        final IBugmateList.Model bugmateListModel;
        final JComponent helpRequest;

        @NotNull
        private final ComponentAnimationController myAnimationController;

        ContentPanel(IHelpOthersCTA.Model ctaModel, IBugmateList.Model bugmateListModel, IMyHelpRequest.Model myHelpRequestModel, IAskForHelp.Model askForHelpModel) {
            myAnimationController = new ComponentAnimationController(this);
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

        void setContent(List<MarkableTipHit> tipHits) {
            removeAll();
            listPanel.setContent(tipHits);
            if (tipHits.size() > 0 && bugmateListModel.bugmateHits.size() > 0) {
                setLayout(new MigLayout("fillx", "20px[fill]0", "0[]20px[]20px[]10px[]20px"));
                add(listPanel, "cell 0 0");
                add(writeTip, "cell 0 1");
                add(bugmateList, "cell 0 2");
                add(helpRequest, "cell 0 3, align center, growx");
            } else if (tipHits.size() == 0 && bugmateListModel.bugmateHits.size() > 0) {
                setLayout(new MigLayout("fillx", "20px[fill]0", "0[]20px[]10px[]20px"));
                add(cta, "cell 0 0");
                add(bugmateList, "cell 0 1");
                add(helpRequest, "cell 0 2, align center, growx");
            } else if (tipHits.size() > 0 && bugmateListModel.bugmateHits.size() == 0) {
                setLayout(new MigLayout("fillx", "20px[fill]0", "0[]20px[]10px[]20px"));
                add(listPanel, "cell 0 0");
                add(writeTip, "cell 0 1");
                add(helpRequest, "cell 0 2, align center, growx");
            } else {
                setLayout(new MigLayout("fillx", "20px[fill]0", "0[]10px[]20px"));
                add(cta, "cell 0 0");
                add(helpRequest, "cell 0 1, align center, growx");
            }
            validate();
        }

        void setZeroFromOneTipContent() {
            removeAll();
            if (bugmateListModel.bugmateHits.size() > 0) {
                setLayout(new MigLayout("fillx", "20px[fill]0", "0[]20px[]10px[]20px"));
                add(writeTip, "cell 0 0");
                add(bugmateList, "cell 0 1");
                add(helpRequest, "cell 0 2, align center, growx");
            } else {
                setLayout(new MigLayout("fillx", "20px[fill]0", "0[]10px[]20px"));
                add(writeTip, "cell 0 0");
                add(helpRequest, "cell 0 1, align center, growx");
            }
            validate();
        }

        ControllableAnimation addTip(final MarkableTipHit newTip, final List<MarkableTipHit> tipHits) {
            PaintableAnimation myAnimation = new GrowFromTop(newTip.getPreferredSize().height);
            myAnimation.runBeforeStart(new Runnable() {
                @Override
                public void run() {
                    tipHits.add(0, newTip);
                    setContent(tipHits);
                }
            });
            myAnimationController.prepareNewAnimation(myAnimation);
            ControllableAnimation tipFadeInAnimation = newTip.fadeIn();
            return myAnimation.with(tipFadeInAnimation);
        }

        @Override
        public void paint(Graphics g) {
            myAnimationController.paint(g);
        }

        @Override
        public void paintOriginalComponent(Graphics g) {
            super.paint(g);
        }

        private final class GrowFromTop extends DynamicallyUpdatedGrowFromTopAnimation {

            GrowFromTop(int growPixels) {
                super(30, ContentPanel.this, growPixels);
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

        void setContent(List<MarkableTipHit> tipHits) {
            removeAll();
            for (int i = 0; i < tipHits.size(); i++) {
                if (i == 0) add(Box.createRigidArea(new Dimension(0, 10)));
                else add(Box.createRigidArea(new Dimension(0, 20)));
                MarkableTipHit hit = tipHits.get(i);
                add(hit);
            }
        }
    }

    private final class MyFadeInAnimation extends FadeInAnimation {

        MyFadeInAnimation(int totalFrames) {
            super(TipResultsTab.this, totalFrames);
            runBeforeStart(new Runnable() {
                @Override
                public void run() {
                    if (tipHits.isEmpty()) {
                        contentPanel.setZeroFromOneTipContent();
                    }
                }
            });
        }

        @Override
        protected void doFinish() {
            TipResultsTab.this.repaint();
        }
    }

    private final class MyFadeOutAnimation extends FadeOutAnimation {

        MyFadeOutAnimation(int totalFrames) {
            super(TipResultsTab.this, totalFrames);
        }

        @Override
        protected void doFinish() {
            TipResultsTab.this.repaint();
        }
    }
}
