package com.samebug.clients.idea.notification;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.project.Project;
import com.samebug.clients.idea.resources.SamebugIcons;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/**
 * Created by poroszd on 2/15/16.
 */
public class OperationalNotification extends Notification {
    public OperationalNotification(final Project project, String title, String content) {
        super(SamebugNotifications.SAMEBUG_OPERATIONAL_NOTIFICATIONS,
                title,
                content,
                NotificationType.INFORMATION,
                SamebugNotifications.basicLinkHandler(project));
    }

    @Nullable
    @Override
    public Icon getIcon() {
        return SamebugIcons.notification;
    }
}
