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
package com.samebug.clients.idea.ui.controller.form;

import com.intellij.openapi.diagnostic.Logger;
import com.samebug.clients.common.services.ChatService;
import com.samebug.clients.common.ui.component.community.IAskForHelpViaChat;
import com.samebug.clients.common.ui.frame.IFrame;
import com.samebug.clients.common.ui.modules.MessageService;
import com.samebug.clients.http.entities.chat.ChatRoom;
import com.samebug.clients.http.entities.helprequest.NewChatRoom;
import com.samebug.clients.http.exceptions.SamebugClientException;
import com.samebug.clients.http.form.CreateChatRoom;
import com.samebug.clients.idea.components.application.IdeaSamebugPlugin;
import org.jetbrains.annotations.NotNull;

public abstract class NewChatFormHandler extends PostFormHandler<ChatRoom, CreateChatRoom.BadRequest> {
    private static final Logger LOGGER = Logger.getInstance(NewChatFormHandler.class);
    @NotNull
    final IFrame frame;
    @NotNull
    final IAskForHelpViaChat form;
    @NotNull
    final NewChatRoom data;
    @NotNull
    final Integer searchId;

    public NewChatFormHandler(@NotNull final IFrame frame, @NotNull final IAskForHelpViaChat form, @NotNull final NewChatRoom data, @NotNull final Integer searchId) {
        this.frame = frame;
        this.form = form;
        this.data = data;
        this.searchId = searchId;
    }

    @Override
    protected void beforePostForm() {
        form.startChat();
    }

    @NotNull
    @Override
    protected ChatRoom postForm() throws SamebugClientException, CreateChatRoom.BadRequest {
        final ChatService chatService = IdeaSamebugPlugin.getInstance().chatService;
        ChatRoom existingRoom = chatService.chatRoom(searchId);
        if (existingRoom == null) return chatService.createChatRoom(searchId, data);
        else return existingRoom;
    }

    @Override
    protected void handleBadRequest(@NotNull CreateChatRoom.BadRequest fieldErrors) {
        for (CreateChatRoom.ErrorCode errorCode : fieldErrors.errorList.getErrorCodes()) {
            LOGGER.warn("Unhandled error code " + errorCode);
        }
        form.failStartChat(null);
    }

    @Override
    protected void handleOtherClientExceptions(@NotNull SamebugClientException exception) {
        frame.popupError(MessageService.message("samebug.component.mark.create.error.unhandled"));
        form.failStartChat(null);
    }
}
