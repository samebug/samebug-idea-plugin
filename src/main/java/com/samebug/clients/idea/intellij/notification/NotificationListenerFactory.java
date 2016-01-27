package com.samebug.clients.idea.intellij.notification;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationListener;
import org.jetbrains.annotations.NotNull;

import javax.swing.event.HyperlinkEvent;

class NotificationListenerFactory {
    public static NotificationListener createListener(final NotificationActionListener actionListener) {
        if (actionListener == null) return null;
        return new NotificationListener() {
            @Override
            public void hyperlinkUpdate(@NotNull Notification notification, @NotNull HyperlinkEvent hyperlinkEvent) {
                String action = hyperlinkEvent.getDescription();
                HyperlinkEvent.EventType eventType = hyperlinkEvent.getEventType();
                if (eventType == HyperlinkEvent.EventType.ACTIVATED) {
                    actionListener.actionActivated(action);
                }
            }
        };
    }
}
