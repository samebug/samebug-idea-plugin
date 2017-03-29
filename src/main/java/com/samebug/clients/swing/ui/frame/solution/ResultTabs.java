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

import com.samebug.clients.common.ui.component.hit.ITipHit;
import com.samebug.clients.common.ui.frame.solution.IResultTabs;
import com.samebug.clients.swing.ui.base.animation.Animator;
import com.samebug.clients.swing.ui.base.animation.ComponentAnimation;
import com.samebug.clients.swing.ui.base.animation.LazyComponentAnimation;
import com.samebug.clients.swing.ui.base.tabbedPane.LabelAndHitsTabHeader;
import com.samebug.clients.swing.ui.base.tabbedPane.SamebugTabbedPane;
import com.samebug.clients.swing.ui.modules.MessageService;

public final class ResultTabs extends SamebugTabbedPane implements IResultTabs {
    private int tipHits;
    private int webHits;

    final WebResultsTab webResultsTab;
    final TipResultsTab tipResultsTab;
    final LabelAndHitsTabHeader webResultsTabHeader;
    final LabelAndHitsTabHeader tipResultsTabHeader;

    public ResultTabs(Model model) {
        tipHits = model.tipResults.tipHits.size();
        webHits = model.webResults.webHits.size();

        webResultsTab = new WebResultsTab(model.webResults, model.cta);
        tipResultsTab = new TipResultsTab(model.tipResults, model.cta);

        tipResultsTabHeader = (LabelAndHitsTabHeader) addLabeledTab(MessageService.message("samebug.component.solutionFrame.tips.tabName"), tipHits, tipResultsTab);
        webResultsTabHeader = (LabelAndHitsTabHeader) addLabeledTab(MessageService.message("samebug.component.solutionFrame.webSolutions.tabName"), webHits, webResultsTab);
        // TODO setSelectedIndex(0) does not work because that is already selected
        tipResultsTabHeader.setSelected(true);
    }

    public void animatedAddTip(ITipHit.Model model) {
        new ShowNewTipAnimation(model).resume();
    }

    private final class ShowNewTipAnimation extends Animator {
        private static final int CycleDuration = 300;
        private static final int WebFadeOutFrames = 10;
        private static final int TipFadeInFrames = 10;
        private static final int TipFloatInFrames = 10;

        private ComponentAnimation animationChain;

        public ShowNewTipAnimation(final ITipHit.Model model) {
            super("ShowNewTipAnimation", WebFadeOutFrames + TipFadeInFrames + TipFloatInFrames, CycleDuration, false);

            ComponentAnimation fadeOutWebTab = webResultsTab.fadeOut(WebFadeOutFrames)
                    .with(webResultsTabHeader.animatedSetSelected(false, WebFadeOutFrames));

            // TODO tip header fade in should be paralell with tip tab fade in, not the whole add tip animation
            animationChain = fadeOutWebTab.andThen(new LazyComponentAnimation(TipFadeInFrames + TipFloatInFrames) {
                @Override
                public ComponentAnimation createAnimation() {
                    ComponentAnimation fadeInTipTab = tipResultsTab.animatedAddTip(model, TipFadeInFrames, TipFloatInFrames)
                            .with(tipResultsTabHeader.animatedSetSelected(true, TipFadeInFrames + TipFloatInFrames));
                    tipResultsTab.revalidate();
                    tipResultsTab.repaint();
                    setSelectedIndex(0);
                    return fadeInTipTab;
                }
            });
        }

        @Override
        public void paintNow(int frame, int totalFrames, int cycle) {
            animationChain.setFrame(frame);
            repaint();
        }

        @Override
        public void animationDone() {
            super.animationDone();
            animationChain.finish();
        }
    }
}
