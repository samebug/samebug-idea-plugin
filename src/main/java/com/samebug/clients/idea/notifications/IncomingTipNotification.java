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
package com.samebug.clients.idea.notifications;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.samebug.clients.common.ui.modules.MessageService;
import com.samebug.clients.http.entities.notification.IncomingAnswer;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class IncomingTipNotification extends Notification {
    public static final String GroupId = "Samebug tip";

    public IncomingTipNotification(IncomingAnswer incomingTip) {
        super(GroupId,
                MessageService.message("samebug.notification.incomingTip.title", incomingTip.getSolution().getDocument().getAuthor().getDisplayName()),
                incomingTip.getSolution().getDocument().getMessage(),
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
}
