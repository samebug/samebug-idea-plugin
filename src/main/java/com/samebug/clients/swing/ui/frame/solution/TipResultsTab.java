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

import com.samebug.clients.common.ui.component.community.IHelpOthersCTA;
import com.samebug.clients.common.ui.component.hit.ITipHit;
import com.samebug.clients.common.ui.frame.solution.ITipResultsTab;
import com.samebug.clients.swing.ui.base.animation.ComponentAnimation;
import com.samebug.clients.swing.ui.base.animation.FadeInAnimation;
import com.samebug.clients.swing.ui.base.animation.LazyComponentAnimation;
import com.samebug.clients.swing.ui.base.animation.Sampler;
import com.samebug.clients.swing.ui.base.panel.SamebugPanel;
import com.samebug.clients.swing.ui.base.panel.TransparentPanel;
import com.samebug.clients.swing.ui.base.scrollPane.SamebugScrollPane;
import com.samebug.clients.swing.ui.component.bugmate.BugmateList;
import com.samebug.clients.swing.ui.component.bugmate.RequestHelp;
import com.samebug.clients.swing.ui.component.bugmate.RevokeHelpRequest;
import com.samebug.clients.swing.ui.component.community.writeTip.WriteTip;
import com.samebug.clients.swing.ui.component.hit.TipHit;
import com.samebug.clients.swing.ui.modules.DrawService;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public final class TipResultsTab extends TransparentPanel implements ITipResultsTab {
    private final Model model;
    private final IHelpOthersCTA.Model ctaModel;

    private final JScrollPane scrollPane;
    private final ContentPanel contentPanel;
    private final List<TipHit> tipHits;

    private ComponentAnimation myAnimation;

    public TipResultsTab(Model model, IHelpOthersCTA.Model ctaModel) {
        this.model = new Model(model);
        this.ctaModel = new IHelpOthersCTA.Model(ctaModel);

        tipHits = new ArrayList<TipHit>();
        for (int i = 0; i < model.tipHits.size(); i++) {
            TipHit.Model m = model.tipHits.get(i);
            TipHit hit = new TipHit(m);
            tipHits.add(hit);
        }
        contentPanel = new ContentPanel();
        scrollPane = new SamebugScrollPane();
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setViewportView(contentPanel);

        setLayout(new BorderLayout());
        add(scrollPane);
    }

    @Override
    public void paint(Graphics g) {
        if (myAnimation == null) super.paint(g);
        else myAnimation.paint(g);
    }

    public ComponentAnimation fadeIn(int totalFrames) {
        myAnimation = new MyFadeInAnimation(totalFrames);
        return myAnimation;
    }

    public ComponentAnimation animatedAddTip(ITipHit.Model model, final int tabFadeInFrames, final int tipFloatInFrames) {
        final TipHit newTip = new TipHit(model);
        tipHits.add(0, newTip);
        contentPanel.listPanel.update();
        contentPanel.update();

        // TODO I have to understand what happens here. Removing any of these makes the animation jump. Also, substituting them with revalidate makes it jump.
        contentPanel.invalidate();
        contentPanel.validate();
        contentPanel.invalidate();
        contentPanel.validate();

        final ComponentAnimation floatInTip = contentPanel.addTip(newTip, tipFloatInFrames);
        myAnimation = new MyFadeInAnimation(tabFadeInFrames);

        return myAnimation.andThen(new LazyComponentAnimation(tipFloatInFrames) {
            @Override
            public ComponentAnimation createAnimation() {
                return floatInTip;
            }
        });
    }

    private final class ContentPanel extends SamebugPanel {
        final ListPanel listPanel;
        final WriteTip writeTip;
        final WriteTip cta;
        final BugmateList bugmateList;
        final JComponent helpRequest;

        private ComponentAnimation contentPanelAnimation;

        {
            listPanel = new ListPanel();
            writeTip = new WriteTip(ctaModel, WriteTip.CTA_TYPE.SMALL);
            cta = new WriteTip(ctaModel, WriteTip.CTA_TYPE.LARGE_FOR_TIP_HITS);
            bugmateList = new BugmateList(model.bugmateList);
            if (model.myHelpRequest != null) {
                helpRequest = new RevokeHelpRequest(model.myHelpRequest);
            } else {
                helpRequest = new RequestHelp(model.askForHelp);
            }
            update();
        }

        void update() {
            removeAll();
            if (tipHits.size() > 0 && model.bugmateList.bugmateHits.size() > 0) {
                setLayout(new MigLayout("fillx", "20[fill]0", "0[]20[]20[]10[]20"));
                add(listPanel, "cell 0 0");
                add(writeTip, "cell 0 1");
                add(bugmateList, "cell 0 2");
                add(helpRequest, "cell 0 3, align center, growx");
            } else if (tipHits.size() == 0 && model.bugmateList.bugmateHits.size() > 0) {
                setLayout(new MigLayout("fillx", "20[fill]0", "0[]20[]10[]20"));
                add(cta, "cell 0 0");
                add(bugmateList, "cell 0 1");
                add(helpRequest, "cell 0 2, align center, growx");
            } else if (tipHits.size() > 0 && model.bugmateList.bugmateHits.size() == 0) {
                setLayout(new MigLayout("fillx", "20[fill]0", "0[]20[]10[]20"));
                add(listPanel, "cell 0 0");
                add(writeTip, "cell 0 1");
                add(helpRequest, "cell 0 2, align center, growx");
            } else {
                setLayout(new MigLayout("fillx", "20[fill]0", "0[]10[]20"));
                add(cta, "cell 0 0");
                add(helpRequest, "cell 0 1, align center, growx");
            }
        }

        ComponentAnimation addTip(TipHit tip, int totalFrames) {
            contentPanelAnimation = new GrowFromTopAndFadeInNewElement(tip, totalFrames, tip.getHeight() + 20);
            return contentPanelAnimation;
        }

        @Override
        public void paint(Graphics g) {
            if (contentPanelAnimation == null) super.paint(g);
            else contentPanelAnimation.paint(g);
        }

        private void superPaint(Graphics g) {
            super.paint(g);
        }

        private final class GrowFromTopAndFadeInNewElement extends ComponentAnimation {
            protected final Dimension myGrownSize;
            protected final int growPixels;
            protected final int[] offsets;
            protected int currentOffset;

            private final ComponentAnimation tipFadeInAnimation;

            public GrowFromTopAndFadeInNewElement(TipHit tip, int totalFrames, int growPixels) {
                super(totalFrames);
                this.myGrownSize = getSize();
                this.growPixels = growPixels;
                assert myGrownSize.height >= growPixels : "Cannot grow " + growPixels + " pixels because its final size is less than that";
                this.offsets = Sampler.easeInOutCubic(growPixels, totalFrames);

                tipFadeInAnimation = tip.fadeIn(totalFrames);
                currentOffset = this.growPixels;
            }

            @Override
            public final void doUpdateFrame(int frame) {
                tipFadeInAnimation.setFrame(frame);
                currentOffset = growPixels - offsets[frame];
                setSize(new Dimension(myGrownSize.width, myGrownSize.height - currentOffset));
                revalidate();
            }

            @Override
            public final void doPaint(Graphics g) {
                BufferedImage myComponentImage = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
                Graphics2D graphics = myComponentImage.createGraphics();
                ContentPanel.this.superPaint(graphics);
                graphics.dispose();

                Graphics2D g2 = DrawService.init(g);
                g2.drawImage(myComponentImage,
                        0, 0, getWidth(), getHeight(),
                        0, currentOffset, getWidth(), currentOffset + getHeight(),
                        ContentPanel.this);
            }

            @Override
            protected void doFinish() {
                tipFadeInAnimation.finish();
                contentPanelAnimation = null;
                revalidate();
                repaint();
            }
        }
    }

    private final class ListPanel extends SamebugPanel {
        {
            setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
            update();
        }

        public void update() {
            removeAll();
            // tipHits is required to be initialized here (the hit views are actually added to the list)
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
        }

        @Override
        protected void doFinish() {
            TipResultsTab.this.myAnimation = null;
        }
    }
}
