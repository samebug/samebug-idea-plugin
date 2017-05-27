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
package com.samebug.clients.idea.notifications;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.samebug.clients.common.ui.modules.MessageService;
import com.samebug.clients.http.entities.notification.IncomingHelpRequest;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class IncomingHelpRequestNotification extends Notification {
    public static final String GroupId = "Samebug help requests";

    public IncomingHelpRequestNotification(IncomingHelpRequest helpRequest) {
        super(GroupId,
                MessageService.message("samebug.component.helpRequest.incoming.title", getRequesterName(helpRequest)),
                getContext(helpRequest),
                NotificationType.INFORMATION,
                null);

        whenExpired(new Runnable() {
            @Override
            public void run() {
                hideBalloon();
            }
        });
    }

    @Nullable
    @Override
    public Icon getIcon() {
        return null;
    }


    private static String getContext(IncomingHelpRequest helpRequest) {
        String context = helpRequest.getMatch().getHelpRequest().getContext();
        return context == null ? MessageService.message("samebug.notification.incomingHelpRequest.noContext") : context;
    }

    private static String getRequesterName(IncomingHelpRequest helpRequest) {
        return helpRequest.getMatch().getHelpRequest().getRequester().getDisplayName();
    }
}
