package com.samebug.clients.idea.notification;

import com.intellij.ide.BrowserUtil;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationDisplayType;
import com.intellij.notification.NotificationListener;
import com.intellij.notification.NotificationsConfiguration;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindowManager;
import org.jetbrains.annotations.NotNull;

import javax.swing.event.HyperlinkEvent;

/**
 * Created by poroszd on 2/15/16.
 */
public class SamebugNotifications {
    public static final String SAMEBUG_SEARCH_NOTIFICATIONS = "Samebug Search Notifications";
    public static final String SAMEBUG_OPERATIONAL_NOTIFICATIONS = "Samebug Operational Notifications";

    public static void registerNotificationGroups() {
        NotificationsConfiguration.getNotificationsConfiguration().register(SAMEBUG_SEARCH_NOTIFICATIONS, NotificationDisplayType.BALLOON, false);
        NotificationsConfiguration.getNotificationsConfiguration().register(SAMEBUG_OPERATIONAL_NOTIFICATIONS, NotificationDisplayType.STICKY_BALLOON, true);
    }


    public final static String SHOW = "#showToolWindow";

    public static NotificationListener basicLinkHandler(final Project project) {
        return new NotificationListener() {
            @Override
            public void hyperlinkUpdate(@NotNull Notification notification, @NotNull HyperlinkEvent hyperlinkEvent) {
                HyperlinkEvent.EventType eventType = hyperlinkEvent.getEventType();
                String action = hyperlinkEvent.getDescription();
                if (eventType == HyperlinkEvent.EventType.ACTIVATED && hyperlinkEvent.getURL() != null) {
                    BrowserUtil.browse(hyperlinkEvent.getURL());
                } else if (eventType == HyperlinkEvent.EventType.ACTIVATED && SHOW.equals(action)) {
                    ToolWindowManager.getInstance(project).getToolWindow("Samebug").show(null);
                    notification.expire();
                }
            }
        };
    }

}
