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
package com.samebug.clients.idea.ui.controller.solution;

import com.samebug.clients.common.ui.component.community.IHelpOthersCTA;
import com.samebug.clients.common.ui.component.hit.ITipHit;
import com.samebug.clients.common.ui.frame.solution.IResultTabs;
import com.samebug.clients.http.entities.search.NewSearchHit;
import com.samebug.clients.http.entities.search.SearchHit;
import com.samebug.clients.http.entities.solution.NewSolution;
import com.samebug.clients.http.entities.solution.NewTip;
import com.samebug.clients.http.entities.solution.SamebugTip;
import com.samebug.clients.idea.ui.controller.form.CreateTipFormHandler;
import com.samebug.clients.swing.ui.frame.solution.ResultTabs;
import com.samebug.clients.swing.ui.modules.ComponentService;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

final class HelpOthersCTAListener implements IHelpOthersCTA.Listener {
    @NotNull
    final SolutionFrameController controller;

    HelpOthersCTAListener(@NotNull final SolutionFrameController controller) {
        this.controller = controller;
    }

    @Override
    public void postTip(@NotNull final IHelpOthersCTA source, @NotNull final String tipBody) {
        NewSearchHit formData = new NewSearchHit(new NewSolution(new NewTip(tipBody, null)));
        new CreateTipFormHandler(controller.view, source, formData, controller.searchId) {
            @Override
            protected void afterPostForm(@NotNull SearchHit<SamebugTip> response) {
                ITipHit.Model tip = controller.conversionService.tipHit(response, false);

                IHelpOthersCTA writeTip = ComponentService.findAncestor((Component) source, IHelpOthersCTA.class);
                assert writeTip != null;
                IResultTabs resultTabs = ComponentService.findAncestor((Component) writeTip, IResultTabs.class);
                assert resultTabs != null;

                writeTip.successPostTip(tip);
                // TODO move this method to interface
                ((ResultTabs) resultTabs).animatedAddTip(tip);
            }
        }.execute();
    }
}
