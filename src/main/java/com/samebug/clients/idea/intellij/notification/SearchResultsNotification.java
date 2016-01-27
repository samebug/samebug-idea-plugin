package com.samebug.clients.idea.intellij.notification;

import com.intellij.notification.NotificationType;
import com.samebug.clients.idea.messages.SamebugBundle;
import org.jetbrains.annotations.Nullable;

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


    private int NOTIFICATION_EXPIRATION_DELAY = 10000;
    public final static String SHOW = "#show";
}
