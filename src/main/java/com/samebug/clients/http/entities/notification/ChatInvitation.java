package com.samebug.clients.http.entities.notification;

import com.samebug.clients.http.entities.chat.ChatOnSearch;
import com.samebug.clients.http.entities.chat.InvitedForSameWorkspace;
import com.samebug.clients.http.entities.user.SamebugUser;

public final class ChatInvitation extends Notification {
    private SamebugUser inviter;
    private String chatRoomId;
    private ChatOnSearch invitationSource;
    private InvitedForSameWorkspace invitationCause;

    public SamebugUser getInviter() {
        return inviter;
    }

    public String getChatRoomId() {
        return chatRoomId;
    }

    public ChatOnSearch getInvitationSource() {
        return invitationSource;
    }

    public InvitedForSameWorkspace getInvitationCause() {
        return invitationCause;
    }
}
