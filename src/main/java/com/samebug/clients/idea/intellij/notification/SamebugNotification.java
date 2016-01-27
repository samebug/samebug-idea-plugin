package com.samebug.clients.idea.intellij.notification;

import com.intellij.notification.*;
import com.samebug.clients.idea.messages.SamebugIcons;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public abstract class SamebugNotification extends Notification {

    public SamebugNotification(@NotNull String title, @NotNull String content, @NotNull NotificationType type, NotificationListener listener) {
        super(SAMEBUG_NOTIFICATION_GROUP, title, content, type, listener);
    }

    public static String SAMEBUG_NOTIFICATION_GROUP = "Sambug Notifications";

    public static void registerNotificationGroups() {
        NotificationsConfiguration.getNotificationsConfiguration().register(SAMEBUG_NOTIFICATION_GROUP, NotificationDisplayType.BALLOON, false);
    }

    @Nullable
    @Override
    public Icon getIcon() {
        return SamebugIcons.samebugNotification;
    }

    @NotNull
    @Override
    public NotificationType getType() {
        return super.getType();
    }

}
