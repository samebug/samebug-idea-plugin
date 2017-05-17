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

import com.samebug.clients.common.ui.component.hit.ITipHit;
import com.samebug.clients.common.ui.frame.helpRequest.IHelpRequestTabs;
import com.samebug.clients.idea.tracking.Events;
import com.samebug.clients.swing.ui.base.animation.ControllableAnimation;
import com.samebug.clients.swing.ui.base.animation.SequenceAnimator;
import com.samebug.clients.swing.ui.base.tabbedPane.SamebugTabHeader;
import com.samebug.clients.swing.ui.base.tabbedPane.SamebugTabbedPane;
import com.samebug.clients.swing.ui.modules.MessageService;
import com.samebug.clients.swing.ui.modules.TrackingService;
import org.jetbrains.annotations.NotNull;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public final class HelpRequestTabs extends SamebugTabbedPane implements IHelpRequestTabs {
    private final WebResultsTab webResultsTab;
    private final HelpRequestTab helpRequestTab;
    private final SamebugTabHeader webResultsTabHeader;
    private final SamebugTabHeader helpRequestTabHeader;

    public HelpRequestTabs(Model model) {
        webResultsTab = new WebResultsTab(model.webResults, model.cta);
        helpRequestTab = new HelpRequestTab(model.helpRequest);

        helpRequestTabHeader = addLabeledTab(MessageService.message("samebug.component.helpRequestTab.tabName"), null, helpRequestTab);
        webResultsTabHeader = addLabeledTab(MessageService.message("samebug.component.webResultsTab.tabName"), model.webResults.webHits.size(), webResultsTab);
        helpRequestTabHeader.setSelected(true);

        addChangeListener(new TabChangeTracker());
    }

    @Override
    public void sentResponse(@NotNull final ITipHit.Model tip) {
        final int CycleDuration = 600;

        // NOTE if the answer was not written as a help request response, ignore it
        if (getSelectedIndex() == 0) {
            ControllableAnimation animationChain = helpRequestTab.animatedAddResponse(tip);
            new SequenceAnimator(animationChain, CycleDuration).resume();
        }
    }

    final class TabChangeTracker implements ChangeListener {
        @Override
        public void stateChanged(ChangeEvent e) {
            String dialogType = getSelectedIndex() == 0 ? "HelpRequest" : "WebHits";
            TrackingService.trace(Events.helpRequestDialogSwitched(dialogType));
        }
    }
}
