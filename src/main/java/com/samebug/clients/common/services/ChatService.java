package com.samebug.clients.common.services;

import com.samebug.clients.http.entities.chat.ChatRoom;
import com.samebug.clients.http.entities.helprequest.NewChatRoom;
import com.samebug.clients.http.exceptions.SamebugClientException;
import com.samebug.clients.http.form.CreateChatRoom;
import org.jetbrains.annotations.NotNull;

public final class ChatService {
    @NotNull
    final ClientService clientService;

    public ChatService(@NotNull final ClientService clientService) {
        this.clientService = clientService;
    }

    @NotNull
    public ChatRoom createChatRoom(@NotNull final Integer searchId, @NotNull final NewChatRoom data) throws SamebugClientException, CreateChatRoom.BadRequest {
        return clientService.getClient().createNewChat(searchId, data);
    }
}
