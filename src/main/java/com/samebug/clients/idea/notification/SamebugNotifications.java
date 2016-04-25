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

import com.intellij.ide.BrowserUtil;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationDisplayType;
import com.intellij.notification.NotificationListener;
import com.intellij.notification.NotificationsConfiguration;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import com.samebug.clients.idea.components.application.Tracking;
import com.samebug.clients.idea.tracking.Events;
import com.samebug.clients.idea.ui.controller.HistoryTabController;
import com.samebug.clients.idea.ui.controller.SearchTabControllers;
import org.jetbrains.annotations.NotNull;

import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

/**
 * Created by poroszd on 2/15/16.
 */
public class SamebugNotifications {
    public static final String SAMEBUG_SEARCH_NOTIFICATIONS = "Samebug Search Notifications";

    public static void registerNotificationGroups() {
        NotificationsConfiguration.getNotificationsConfiguration().register(SAMEBUG_SEARCH_NOTIFICATIONS, NotificationDisplayType.BALLOON, false);
    }


    public final static String HISTORY = "#history";
    public final static String SEARCH = "#search-";

    public static NotificationListener basicNotificationListener(final Project project, final String categoryForTracking) {
        return new NotificationListener() {
            @Override
            public void hyperlinkUpdate(@NotNull Notification notification, @NotNull HyperlinkEvent hyperlinkEvent) {
                HyperlinkEvent.EventType eventType = hyperlinkEvent.getEventType();
                String action = hyperlinkEvent.getDescription();
                if (eventType == HyperlinkEvent.EventType.ACTIVATED && hyperlinkEvent.getURL() != null) {
                    BrowserUtil.browse(hyperlinkEvent.getURL());
                    Tracking.projectTracking(project).trace(Events.linkClick(project, hyperlinkEvent.getURL()));
                } else if (eventType == HyperlinkEvent.EventType.ACTIVATED && HISTORY.equals(action)) {
                    ServiceManager.getService(project, HistoryTabController.class).focus();
                    notification.expire();
                    Tracking.projectTracking(project).trace(Events.toolWindowOpen(project, categoryForTracking));
                } else if (eventType == HyperlinkEvent.EventType.ACTIVATED && action.startsWith(SEARCH)) {
                    int searchId = Integer.parseInt(action.substring(SEARCH.length()));
                    ServiceManager.getService(project, SearchTabControllers.class).openSearchTab(searchId);
                    notification.expire();
                    Tracking.projectTracking(project).trace(Events.toolWindowOpen(project, categoryForTracking));
                }
            }
        };
    }

    public static HyperlinkListener basicHyperlinkListener(final Project project, final String categoryForTracking) {
        return new HyperlinkListener() {
            @Override
            public void hyperlinkUpdate(HyperlinkEvent hyperlinkEvent) {
                HyperlinkEvent.EventType eventType = hyperlinkEvent.getEventType();
                String action = hyperlinkEvent.getDescription();
                if (eventType == HyperlinkEvent.EventType.ACTIVATED && hyperlinkEvent.getURL() != null) {
                    BrowserUtil.browse(hyperlinkEvent.getURL());
                    Tracking.projectTracking(project).trace(Events.linkClick(project, hyperlinkEvent.getURL()));
                } else if (eventType == HyperlinkEvent.EventType.ACTIVATED && HISTORY.equals(action)) {
                    ServiceManager.getService(project, HistoryTabController.class).focus();
                    Tracking.projectTracking(project).trace(Events.toolWindowOpen(project, categoryForTracking));
                } else if (eventType == HyperlinkEvent.EventType.ACTIVATED && action.startsWith(SEARCH)) {
                    int searchId = Integer.parseInt(action.substring(SEARCH.length()));
                    ServiceManager.getService(project, SearchTabControllers.class).openSearchTab(searchId);
                    Tracking.projectTracking(project).trace(Events.toolWindowOpen(project, categoryForTracking));
                }
            }
        };
    }
}
