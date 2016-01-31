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
package com.samebug.clients.idea.intellij.notification;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationListener;
import javax.annotation.Nonnull;

import javax.swing.event.HyperlinkEvent;

class NotificationListenerFactory {
    public static NotificationListener createListener(final NotificationActionListener actionListener) {
        if (actionListener == null) return null;
        return new NotificationListener() {
            @Override
            public void hyperlinkUpdate(@Nonnull Notification notification, @Nonnull HyperlinkEvent hyperlinkEvent) {
                String action = hyperlinkEvent.getDescription();
                HyperlinkEvent.EventType eventType = hyperlinkEvent.getEventType();
                if (eventType == HyperlinkEvent.EventType.ACTIVATED) {
                    actionListener.actionActivated(action);
                }
            }
        };
    }
}
