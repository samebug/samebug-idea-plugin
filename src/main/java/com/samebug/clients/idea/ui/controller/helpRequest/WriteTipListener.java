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
package com.samebug.clients.idea.ui.controller.helpRequest;

import com.samebug.clients.common.ui.component.community.IHelpOthersCTA;
import com.samebug.clients.http.entities.helprequest.HelpRequest;
import com.samebug.clients.http.entities.solution.SamebugTip;
import com.samebug.clients.http.entities.solution.SolutionSlot;
import com.samebug.clients.http.form.TipCreate;
import com.samebug.clients.idea.ui.controller.form.CreateTipFormHandler;

final class WriteTipListener implements IHelpOthersCTA.Listener {
    final HelpRequestController controller;

    public WriteTipListener(final HelpRequestController controller) {
        this.controller = controller;
    }

    @Override
    public void postTip(final IHelpOthersCTA source, final String tipBody) {
        final HelpRequest helpRequest = controller.helpRequestStore.getHelpRequest(controller.helpRequestId);
        assert helpRequest != null : "we just showed it, it should not be null";
        // TODO matching help request?
//        assert helpRequest.matchingGroup.lastSearchInfo != null : "our own search is always visible";

        new CreateTipFormHandler(controller.view, source, new TipCreate.ForHelpRequest(tipBody, null, helpRequest.getSearchId(), controller.helpRequestId)) {
            @Override
            protected void afterPostForm(SolutionSlot<SamebugTip> response) {
                // TODO animation
                controller.load();
            }
        }.execute();
    }
}
