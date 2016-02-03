/**
 * Copyright 2016 Samebug, Inc.
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
package com.samebug.clients.idea.notification;

import com.intellij.notification.*;
import com.samebug.clients.idea.resources.SamebugIcons;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.event.HyperlinkEvent;

public abstract class SamebugNotification extends Notification {

    protected SamebugNotification(@NotNull String title, @NotNull String content, @NotNull NotificationType type,final NotificationActionListener actionListener) {
        super(SAMEBUG_NOTIFICATION_GROUP, title, content, type, createListener(actionListener));
    }

    private static final String SAMEBUG_NOTIFICATION_GROUP = "Samebug Notifications";

    public static void registerNotificationGroups() {
        NotificationsConfiguration.getNotificationsConfiguration().register(SAMEBUG_NOTIFICATION_GROUP, NotificationDisplayType.BALLOON, false);
    }

    @Nullable
    @Override
    public Icon getIcon() {
        return SamebugIcons.notification;
    }

    @NotNull
    @Override
    public NotificationType getType() {
        return super.getType();
    }

    public static NotificationListener createListener(final NotificationActionListener actionListener) {
        if (actionListener == null) return null;
        return new NotificationListener() {
            @Override
            public void hyperlinkUpdate(@NotNull Notification notification, @NotNull HyperlinkEvent hyperlinkEvent) {
                HyperlinkEvent.EventType eventType = hyperlinkEvent.getEventType();
                if (eventType == HyperlinkEvent.EventType.ACTIVATED) {
                    String action = hyperlinkEvent.getDescription();
                    actionListener.actionActivated(action);
                }
            }
        };
    }
}
