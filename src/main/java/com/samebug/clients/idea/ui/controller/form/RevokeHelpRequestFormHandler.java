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
import com.samebug.clients.common.services.HelpRequestService;
import com.samebug.clients.common.ui.component.helpRequest.IMyHelpRequest;
import com.samebug.clients.common.ui.frame.IFrame;
import com.samebug.clients.http.entities.helprequest.HelpRequest;
import com.samebug.clients.http.exceptions.SamebugClientException;
import com.samebug.clients.http.form.HelpRequestCancel;
import com.samebug.clients.idea.components.application.IdeaSamebugPlugin;
import com.samebug.clients.swing.ui.modules.MessageService;
import org.jetbrains.annotations.NotNull;

public abstract class RevokeHelpRequestFormHandler extends PostFormHandler<HelpRequest, HelpRequestCancel.BadRequest> {
    private static final Logger LOGGER = Logger.getInstance(RevokeHelpRequestFormHandler.class);
    final IFrame frame;
    final IMyHelpRequest button;
    final String helpRequestId;

    public RevokeHelpRequestFormHandler(IFrame frame, IMyHelpRequest button, String helpRequestId) {
        this.frame = frame;
        this.button = button;
        this.helpRequestId = helpRequestId;
    }

    @Override
    protected void beforePostForm() {
        button.startRevoke();
    }

    @NotNull
    @Override
    protected HelpRequest postForm() throws SamebugClientException, HelpRequestCancel.BadRequest {
        final HelpRequestService helpRequestService = IdeaSamebugPlugin.getInstance().helpRequestService;
        return helpRequestService.revokeHelpRequest(helpRequestId);
    }

    @Override
    protected void handleBadRequest(@NotNull HelpRequestCancel.BadRequest fieldErrors) {
        for (HelpRequestCancel.ErrorCode errorCode : fieldErrors.errorList.getErrorCodes()) {
            LOGGER.warn("Unhandled error code " + errorCode);
        }
        frame.popupError(MessageService.message("samebug.component.helpRequest.revoke.error.unhandled"));
        button.failRevoke();
    }

    @Override
    protected void handleOtherClientExceptions(@NotNull SamebugClientException exception) {
        frame.popupError(MessageService.message("samebug.component.helpRequest.revoke.error.unhandled"));
        button.failRevoke();
    }
}
