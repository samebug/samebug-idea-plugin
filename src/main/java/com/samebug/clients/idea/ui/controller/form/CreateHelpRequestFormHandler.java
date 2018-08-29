/*
 * Copyright 2018 Samebug, Inc.
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
import com.samebug.clients.common.ui.component.community.IAskForHelp;
import com.samebug.clients.common.ui.frame.IFrame;
import com.samebug.clients.common.ui.modules.MessageService;
import com.samebug.clients.http.entities.helprequest.HelpRequest;
import com.samebug.clients.http.entities.helprequest.NewHelpRequest;
import com.samebug.clients.http.exceptions.SamebugClientException;
import com.samebug.clients.http.form.HelpRequestCreate;
import com.samebug.clients.idea.components.application.IdeaSamebugPlugin;
import org.jetbrains.annotations.NotNull;

public abstract class CreateHelpRequestFormHandler extends PostFormHandler<HelpRequest, HelpRequestCreate.BadRequest> {
    private static final Logger LOGGER = Logger.getInstance(CreateHelpRequestFormHandler.class);
    final IFrame frame;
    final IAskForHelp form;
    final NewHelpRequest data;
    final Integer searchId;

    public CreateHelpRequestFormHandler(IFrame frame, IAskForHelp form, NewHelpRequest data, @NotNull Integer searchId) {
        this.frame = frame;
        this.form = form;
        this.data = data;
        this.searchId = searchId;
    }

    @Override
    protected void beforePostForm() {
        form.startRequestTip();
    }

    @NotNull
    @Override
    protected HelpRequest postForm() throws SamebugClientException, HelpRequestCreate.BadRequest {
        final HelpRequestService helpRequestService = IdeaSamebugPlugin.getInstance().helpRequestService;
        return helpRequestService.createHelpRequest(searchId, data);
    }

    @Override
    protected void handleBadRequest(@NotNull HelpRequestCreate.BadRequest fieldErrors) {
        IAskForHelp.BadRequest.Context context = null;
        for (HelpRequestCreate.ErrorCode errorCode : fieldErrors.errorList.getErrorCodes()) {
            switch (errorCode) {
                case CONTEXT_TOO_LONG:
                    context = IAskForHelp.BadRequest.Context.TOO_LONG;
                    break;
                default:
                    LOGGER.warn("Unhandled error code " + errorCode);
            }
        }
        if (context != null) {
            IAskForHelp.BadRequest b = new IAskForHelp.BadRequest(context);
            form.failRequestTip(b);
        } else {
            frame.popupError(MessageService.message("samebug.component.helpRequest.ask.error.unhandled"));
            form.failRequestTip(null);
        }
    }

    @Override
    protected void handleOtherClientExceptions(@NotNull SamebugClientException exception) {
        frame.popupError(MessageService.message("samebug.component.helpRequest.ask.error.unhandled"));
        form.failRequestTip(null);
    }
}
