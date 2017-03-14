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

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.util.messages.MessageBusConnection;
import com.samebug.clients.common.api.entities.helpRequest.MatchingHelpRequest;
import com.samebug.clients.common.api.entities.solution.RestHit;
import com.samebug.clients.common.api.entities.solution.Tip;
import com.samebug.clients.common.api.form.CreateTip;
import com.samebug.clients.common.ui.component.community.IHelpOthersCTA;
import com.samebug.clients.idea.ui.controller.form.CreateTipFormHandler;
import com.samebug.clients.idea.ui.modules.IdeaListenerService;

final class WriteTipListener implements IHelpOthersCTA.Listener {
    final static Logger LOGGER = Logger.getInstance(WriteTipListener.class);
    final HelpRequestController controller;

    public WriteTipListener(final HelpRequestController controller) {
        this.controller = controller;

        MessageBusConnection projectConnection = controller.myProject.getMessageBus().connect(controller);
        projectConnection.subscribe(IdeaListenerService.HelpOthersCTA, this);
    }

    @Override
    public void postTip(final IHelpOthersCTA source, final String tipBody) {
        LOGGER.debug("post tips clicked");

        final MatchingHelpRequest helpRequest = controller.helpRequestStore.getHelpRequest(controller.helpRequestId);
        assert helpRequest != null : "we just showed it, it should not be null";
        assert helpRequest.matchingGroup.lastSearchInfo != null : "our own search is always visible";
        new CreateTipFormHandler(controller.view, source, new CreateTip(helpRequest.matchingGroup.lastSearchInfo.id, tipBody, null, controller.helpRequestId)) {
            @Override
            protected void afterPostForm(RestHit<Tip> response) {
                controller.load();
            }
        }.execute();
    }
}