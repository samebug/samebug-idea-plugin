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
import com.samebug.clients.http.entities.solution.NewSolution;
import com.samebug.clients.http.entities.solution.NewTip;
import com.samebug.clients.http.exceptions.SamebugClientException;
import com.samebug.clients.idea.ui.controller.form.CreateTipFormHandler;
import com.samebug.clients.swing.ui.modules.ComponentService;
import com.samebug.clients.swing.ui.modules.MessageService;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;

final class HelpOthersCTAListener implements IHelpOthersCTA.Listener {
    @NotNull
    private final SolutionFrameController controller;

    HelpOthersCTAListener(@NotNull final SolutionFrameController controller) {
        this.controller = controller;
    }

    @Override
    public void postTip(@NotNull final IHelpOthersCTA source, @NotNull final String tipBody) {
        NewSearchHit formData = new NewSearchHit(new NewSolution(new NewTip(tipBody, null)));

        new CreateTipFormHandler(formData, controller.searchId) {
            @Override
            protected void beforePostForm() {
                source.startPostTip();
            }

            @Override
            protected void afterPostFormUI(@NotNull ITipHit.Model tip) {
                IResultTabs resultTabs = ComponentService.findAncestor((Component) source, IResultTabs.class);
                assert resultTabs != null;

                source.successPostTip(tip);
                resultTabs.tipWritten(tip);
            }

            @Override
            protected void handleBadRequestUI(@Nullable IHelpOthersCTA.BadRequest errors) {
                if (errors == null) controller.view.popupError(MessageService.message("samebug.component.tip.write.error.unhandled"));
                source.failPostTipWithFormError(errors);
            }

            @Override
            protected void handleOtherClientExceptions(@NotNull SamebugClientException exception) {
                controller.view.popupError(MessageService.message("samebug.component.tip.write.error.unhandled"));
                source.failPostTipWithFormError(null);
            }
        }.execute();
    }
}
