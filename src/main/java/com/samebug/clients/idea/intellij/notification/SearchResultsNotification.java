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

import com.intellij.notification.NotificationType;
import com.samebug.clients.idea.messages.SamebugBundle;

import javax.annotation.Nullable;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SearchResultsNotification extends SamebugNotification {
    public SearchResultsNotification(String message, @Nullable NotificationActionListener actionListener) {
        super(
                SamebugBundle.message("samebug.notification.searchresults.title"),
                message,
                NotificationType.INFORMATION,
                NotificationListenerFactory.createListener(actionListener)
        );

        whenExpired(new Runnable() {
            @Override
            public void run() {
                hideBalloon();
            }
        });

        Timer timer = new Timer(NOTIFICATION_EXPIRATION_DELAY, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                expire();
            }
        });

        timer.start();
    }


    private final int NOTIFICATION_EXPIRATION_DELAY = 10000;
    public final static String SHOW = "#show";
}
