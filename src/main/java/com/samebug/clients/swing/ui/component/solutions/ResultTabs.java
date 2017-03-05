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

import com.samebug.clients.common.ui.component.solutions.IResultTabs;
import com.samebug.clients.swing.ui.component.util.tabbedPane.SamebugTabHeader;
import com.samebug.clients.swing.ui.component.util.tabbedPane.SamebugTabbedPane;
import com.samebug.clients.swing.ui.global.MessageService;

public final class ResultTabs extends SamebugTabbedPane implements IResultTabs {
    private final Model model;

    private final WebResultsTab webResultsTab;
    private final TipResultsTab tipResultsTab;
    private final SamebugTabHeader webResultsTabHeader;
    private final SamebugTabHeader tipResultsTabHeader;

    public ResultTabs(Model model) {
        this.model = new Model(model);

        webResultsTab = new WebResultsTab(model.webResults, model.cta);
        tipResultsTab = new TipResultsTab(model.tipResults, model.cta);

        tipResultsTabHeader = addTab(MessageService.message("samebug.component.solutionFrame.tips.tabName"), model.tipResults.getTipsSize(), tipResultsTab);
        webResultsTabHeader = addTab(MessageService.message("samebug.component.solutionFrame.webSolutions.tabName"), model.webResults.getHitsSize(), webResultsTab);
        tipResultsTabHeader.setSelected(true);
    }
}
