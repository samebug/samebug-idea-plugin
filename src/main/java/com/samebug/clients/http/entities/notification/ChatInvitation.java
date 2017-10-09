/*
 * Copyright 2017 Samebug, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 *    http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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