/*
 * Copyright 2018 Samebug, Inc.
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

import com.samebug.clients.common.ui.component.community.IAskForHelp;
import com.samebug.clients.common.ui.component.community.IAskForHelpViaChat;
import com.samebug.clients.common.ui.modules.TrackingService;
import com.samebug.clients.http.entities.chat.ChatRoom;
import com.samebug.clients.http.entities.helprequest.HelpRequest;
import com.samebug.clients.http.entities.helprequest.NewChatRoom;
import com.samebug.clients.http.entities.helprequest.NewHelpRequest;
import com.samebug.clients.idea.components.application.IdeaSamebugPlugin;
import com.samebug.clients.idea.ui.controller.form.CreateHelpRequestFormHandler;
import com.samebug.clients.idea.ui.controller.form.NewChatFormHandler;
import com.samebug.clients.idea.ui.modules.BrowserUtil;
import com.samebug.clients.swing.tracking.SwingRawEvent;
import com.samebug.clients.swing.tracking.TrackingKeys;
import com.samebug.clients.swing.ui.modules.DataService;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.net.URI;

final class RequestHelpListener implements IAskForHelp.Listener, IAskForHelpViaChat.Listener {
    final SolutionFrameController controller;

    RequestHelpListener(final SolutionFrameController controller) {
        this.controller = controller;
    }

    @Override
    public void askBugmates(final IAskForHelp source, final String description) {
        final JComponent sourceComponent = (JComponent) source;
        final String transactionId = DataService.getData(sourceComponent, TrackingKeys.HelpRequestTransaction);

        TrackingService.trace(SwingRawEvent.helpRequestSubmit(sourceComponent, transactionId));
        new CreateHelpRequestFormHandler(controller.view, source, new NewHelpRequest(description), controller.searchId) {
            @Override
            protected void afterPostForm(@NotNull HelpRequest response) {
                controller.load();
                TrackingService.trace(SwingRawEvent.helpRequestCreate(sourceComponent, transactionId, response.getId()));
            }
        }.execute();
    }

    @Override
    public void askTeammates(final IAskForHelpViaChat source) {
        final JComponent sourceComponent = (JComponent) source;

        new NewChatFormHandler(controller.view, source, new NewChatRoom(), controller.searchId) {
            @Override
            protected void afterPostForm(@NotNull ChatRoom response) {
                source.successStartChat();
                Integer searchId = response.getSource().getSearch().getId();
                URI searchUrl = IdeaSamebugPlugin.getInstance().uriBuilder.search(searchId);
                TrackingService.trace(SwingRawEvent.chatOpened(sourceComponent, searchId));
                BrowserUtil.browse(searchUrl);
            }
        }.execute();
    }
}
