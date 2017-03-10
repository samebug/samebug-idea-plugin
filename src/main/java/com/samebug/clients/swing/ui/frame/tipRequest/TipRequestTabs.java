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
package com.samebug.clients.swing.ui.frame.tipRequest;

import com.samebug.clients.common.ui.frame.tipRequest.ITipRequestTabs;
import com.samebug.clients.swing.ui.base.tabbedPane.SamebugTabHeader;
import com.samebug.clients.swing.ui.base.tabbedPane.SamebugTabbedPane;
import com.samebug.clients.swing.ui.frame.solution.WebResultsTab;
import com.samebug.clients.swing.ui.modules.MessageService;

public final class TipRequestTabs extends SamebugTabbedPane implements ITipRequestTabs {
    private final WebResultsTab webResultsTab;
    private final TipRequestTab tipRequestTab;
    private final SamebugTabHeader webResultsTabHeader;
    private final SamebugTabHeader tipRequestTabHeader;

    public TipRequestTabs(Model model) {
        webResultsTab = new WebResultsTab(model.webResults, model.cta);
        tipRequestTab = new TipRequestTab(model.tipRequest);

        // TODO hits: 0 should be null, not displaying the number
        tipRequestTabHeader = addTab(MessageService.message("samebug.component.solutionFrame.tips.tabName"), 0, tipRequestTab);
        webResultsTabHeader = addTab(MessageService.message("samebug.component.solutionFrame.webSolutions.tabName"), model.webResults.webHits.size(), webResultsTab);
        tipRequestTabHeader.setSelected(true);
    }
}