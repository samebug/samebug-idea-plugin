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
import com.samebug.clients.idea.tracking.Events;
import com.samebug.clients.swing.ui.base.animation.ControllableAnimation;
import com.samebug.clients.swing.ui.base.animation.SequenceAnimator;
import com.samebug.clients.swing.ui.base.tabbedPane.LabelAndHitsTabHeader;
import com.samebug.clients.swing.ui.base.tabbedPane.SamebugTabbedPane;
import com.samebug.clients.swing.ui.modules.MessageService;
import com.samebug.clients.swing.ui.modules.TrackingService;
import org.jetbrains.annotations.NotNull;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

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

        tipResultsTabHeader = (LabelAndHitsTabHeader) addLabeledTab(MessageService.message("samebug.component.tipResultsTab.tabName"), tipHits, tipResultsTab);
        webResultsTabHeader = (LabelAndHitsTabHeader) addLabeledTab(MessageService.message("samebug.component.webResultsTab.tabName"), webHits, webResultsTab);
        // TODO setSelectedIndex(0) does not work because that is already selected
        tipResultsTabHeader.setSelected(true);

        addChangeListener(new TabChangeTracker());
    }

    public void tipWritten(@NotNull ITipHit.Model model) {
        final int CycleDuration = 600;
        final int FadeOutFrames = 30;
        final int FadeInFrames = 30;

        ControllableAnimation animationChain;
        final ControllableAnimation showTipTabAnimation;
        if (getSelectedIndex() == 0) {
            ControllableAnimation fadeOut = tipResultsTab.fadeOut(FadeOutFrames);
            ControllableAnimation fadeIn = tipResultsTab.fadeIn(FadeInFrames);
            fadeIn.runBeforeStart(new Runnable() {
                @Override
                public void run() {
                    // TODO scroll to top
                }
            });
            showTipTabAnimation = fadeOut.andThen(fadeIn);
        } else {
            ControllableAnimation fadeOut = webResultsTab.fadeOut(FadeOutFrames)
                    .with(webResultsTabHeader.animatedSetSelected(false, FadeOutFrames));
            ControllableAnimation fadeIn = tipResultsTab.fadeIn(FadeInFrames)
                    .with(tipResultsTabHeader.animatedSetSelected(true, FadeInFrames));
            fadeIn.runBeforeStart(new Runnable() {
                @Override
                public void run() {
                    setSelectedIndex(0);
                }
            });
            showTipTabAnimation = fadeOut.andThen(fadeIn);
        }

        ControllableAnimation floatInTip = tipResultsTab.animatedAddTip(model);
        animationChain = showTipTabAnimation.andThen(floatInTip);
        new SequenceAnimator(animationChain, CycleDuration).resume();
    }

    final class TabChangeTracker implements ChangeListener {
        @Override
        public void stateChanged(ChangeEvent e) {
            String dialogType = getSelectedIndex() == 0 ? "Tips" : "WebHits";
            TrackingService.trace(Events.solutionDialogSwitched(dialogType));
        }
    }
}
