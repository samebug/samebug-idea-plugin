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

import com.samebug.clients.common.api.client.RestError;
import com.samebug.clients.common.api.entities.helpRequest.MyHelpRequest;
import com.samebug.clients.common.api.exceptions.SamebugClientException;
import com.samebug.clients.common.api.form.FieldError;
import com.samebug.clients.common.api.form.RevokeHelpRequest;
import com.samebug.clients.common.services.HelpRequestService;
import com.samebug.clients.common.ui.component.form.FormMismatchException;
import com.samebug.clients.common.ui.component.helpRequest.IMyHelpRequest;
import com.samebug.clients.common.ui.frame.IFrame;
import com.samebug.clients.idea.components.application.IdeaSamebugPlugin;
import com.samebug.clients.swing.ui.modules.MessageService;

import java.util.List;

public abstract class RevokeHelpRequestFormHandler extends PostFormHandler<MyHelpRequest> {
    final IFrame frame;
    final IMyHelpRequest button;
    final RevokeHelpRequest data;

    public RevokeHelpRequestFormHandler(IFrame frame, IMyHelpRequest button, RevokeHelpRequest data) {
        this.frame = frame;
        this.button = button;
        this.data = data;
    }

    @Override
    protected void beforePostForm() {
        button.startRevoke();
    }

    @Override
    protected MyHelpRequest postForm() throws SamebugClientException {
        final HelpRequestService helpRequestService = IdeaSamebugPlugin.getInstance().helpRequestService;
        return helpRequestService.revokeHelpRequest(data.helpRequestId);
    }

    @Override
    protected void handleFieldError(FieldError fieldError, List<String> globalErrors, List<FieldError> fieldErrors) {
        super.handleFieldError(fieldError, globalErrors, fieldErrors);
        globalErrors.add(MessageService.message("samebug.error.pluginBug"));
    }

    @Override
    protected void handleNonFormBadRequests(RestError nonFormError, List<String> globalErrors, List<FieldError> fieldErrors) {
        super.handleNonFormBadRequests(nonFormError, globalErrors, fieldErrors);
        if (nonFormError.code.equals(RevokeHelpRequest.E_ALREADY_REVOKED)) globalErrors.add(MessageService.message("samebug.component.helpRequest.revoke.error.alreadyRevoked"));
        else globalErrors.add(MessageService.message("samebug.component.helpRequest.revoke.error.badRequest"));
    }

    @Override
    protected void handleOtherClientExceptions(SamebugClientException exception, List<String> globalErrors, List<FieldError> fieldErrors) {
        super.handleOtherClientExceptions(exception, globalErrors, fieldErrors);
        globalErrors.add(MessageService.message("samebug.component.helpRequest.revoke.error.unhandled"));
    }

    @Override
    protected void showFieldErrors(List<FieldError> fieldErrors) throws FormMismatchException {
        button.failRevoke();
    }

    @Override
    protected void showGlobalErrors(List<String> globalErrors) {
        if (!globalErrors.isEmpty()) frame.popupError(globalErrors.get(0));
    }
}
