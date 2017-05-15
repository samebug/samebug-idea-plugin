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
import com.samebug.clients.common.ui.component.hit.ITipHit;
import com.samebug.clients.http.entities.helprequest.HelpRequest;
import com.samebug.clients.http.entities.helprequest.HelpRequestMatch;
import com.samebug.clients.http.entities.search.NewSearchHit;
import com.samebug.clients.http.entities.search.SearchHit;
import com.samebug.clients.http.entities.solution.NewSolution;
import com.samebug.clients.http.entities.solution.NewTip;
import com.samebug.clients.http.entities.solution.SamebugTip;
import com.samebug.clients.idea.ui.controller.form.CreateTipFormHandler;
import org.jetbrains.annotations.NotNull;

final class WriteTipListener implements IHelpOthersCTA.Listener {
    @NotNull
    final HelpRequestController controller;

    WriteTipListener(final HelpRequestController controller) {
        this.controller = controller;
    }

    @Override
    public void postTip(@NotNull final IHelpOthersCTA source, @NotNull final String tipBody) {
        final HelpRequestMatch match = controller.getHelpRequestMatch();
        final HelpRequest helpRequest = match.getHelpRequest();
        final String helpRequestId = helpRequest.getId();
        final Integer accessibleSearchId = match.getMatchingGroup().getLastSearchId();

        NewSearchHit formData = new NewSearchHit(new NewSolution(new NewTip(tipBody, null), helpRequestId));
        new CreateTipFormHandler(controller.view, source, formData, accessibleSearchId) {
            @Override
            protected void afterPostForm(@NotNull SearchHit<SamebugTip> response) {
                ITipHit.Model tip = controller.conversionService.tipHit(response, false);
                // TODO animation
                source.successPostTip(tip);
            }
        }.execute();
    }
}
