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
        // switch to tips tab
        int currentTab = getSelectedIndex();
        if (currentTab != 0) {
            // TODO setselected is so cluttered
            tipResultsTabHeader.animatedSetSelected(true);
            webResultsTabHeader.animatedSetSelected(false);
            setSelectedIndex(0);
        }

        // update tip hits
        tipHits += 1;
        tipResultsTabHeader.setHits(tipHits);

        // float in new tip
        tipResultsTab.animatedAddTip(model);
    }
}
