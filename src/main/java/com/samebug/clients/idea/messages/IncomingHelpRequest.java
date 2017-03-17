package com.samebug.clients.idea.messages;

import com.intellij.util.messages.Topic;
import com.samebug.clients.common.api.entities.helpRequest.HelpRequest;

public interface IncomingHelpRequest {
    Topic<IncomingHelpRequest> TOPIC = Topic.create("incoming help request", IncomingHelpRequest.class);

    void showHelpRequest(HelpRequest helpRequest);
    void addHelpRequest(HelpRequest helpRequest);
}
