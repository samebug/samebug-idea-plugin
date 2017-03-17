package com.samebug.clients.common.api.websocket;

import com.samebug.clients.common.api.entities.helpRequest.IncomingTip;
import com.samebug.clients.common.api.entities.helpRequest.MatchingHelpRequest;

public interface NotificationHandler {
    void helpRequestReceived(MatchingHelpRequest helpRequestNotification);

    void tipReceived(IncomingTip tipNotification);
}
