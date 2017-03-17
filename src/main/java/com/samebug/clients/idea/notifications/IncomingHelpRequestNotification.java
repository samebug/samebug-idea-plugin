package com.samebug.clients.idea.notifications;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.samebug.clients.common.api.entities.helpRequest.HelpRequest;
import com.samebug.clients.swing.ui.modules.MessageService;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class IncomingHelpRequestNotification extends Notification {
    public static final String GroupId = "Samebug help requests";

    public IncomingHelpRequestNotification(HelpRequest helpRequest) {
        super(GroupId,
                MessageService.message("samebug.component.helpRequest.incoming.title", helpRequest.requester.displayName),
                helpRequest.context,
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
