package com.samebug.clients.idea.messages;

import com.intellij.util.messages.Topic;
import com.samebug.clients.http.entities.notification.ChatInvitation;

public interface IncomingChatInvitation {
    Topic<IncomingChatInvitation> TOPIC = Topic.create("incoming chat invitation", IncomingChatInvitation.class);

    void invitedToChat(ChatInvitation chatInvitation);
}
