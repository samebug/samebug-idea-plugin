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
import com.samebug.clients.common.api.form.CreateHelpRequest;
import com.samebug.clients.common.api.form.FieldError;
import com.samebug.clients.common.services.HelpRequestService;
import com.samebug.clients.common.ui.component.community.IAskForHelp;
import com.samebug.clients.common.ui.component.form.FormMismatchException;
import com.samebug.clients.common.ui.frame.IFrame;
import com.samebug.clients.idea.components.application.IdeaSamebugPlugin;
import com.samebug.clients.swing.ui.modules.MessageService;

import java.util.List;

public abstract class CreateHelpRequestFormHandler extends PostFormHandler<MyHelpRequest> {
    final IFrame frame;
    final IAskForHelp form;
    final CreateHelpRequest data;

    public CreateHelpRequestFormHandler(IFrame frame, IAskForHelp form, CreateHelpRequest data) {
        this.frame = frame;
        this.form = form;
        this.data = data;
    }

    @Override
    protected void beforePostForm() {
        form.startRequestTip();
    }

    @Override
    protected MyHelpRequest postForm() throws SamebugClientException {
        final HelpRequestService helpRequestService = IdeaSamebugPlugin.getInstance().helpRequestService;
        return helpRequestService.createHelpRequest(data.searchId, data.context);
    }

    @Override
    protected void handleFieldError(FieldError fieldError, List<String> globalErrors, List<FieldError> fieldErrors) {
        super.handleFieldError(fieldError, globalErrors, fieldErrors);
        if (CreateHelpRequest.CONTEXT.equals(fieldError.key)) fieldErrors.add(fieldError);
        else globalErrors.add(MessageService.message("samebug.error.pluginBug"));
    }

    @Override
    protected void handleNonFormBadRequests(RestError nonFormError, List<String> globalErrors, List<FieldError> fieldErrors) {
        super.handleNonFormBadRequests(nonFormError, globalErrors, fieldErrors);
        // TODO reload?
        if (nonFormError.code.equals(CreateHelpRequest.E_DUPLICATE_HELP_REQUEST)) globalErrors.add(MessageService.message("samebug.component.helpRequest.ask.error.duplicate"));
        else globalErrors.add(MessageService.message("samebug.component.helpRequest.ask.error.badRequest"));
    }

    @Override
    protected void handleOtherClientExceptions(SamebugClientException exception, List<String> globalErrors, List<FieldError> fieldErrors) {
        super.handleOtherClientExceptions(exception, globalErrors, fieldErrors);
        globalErrors.add(MessageService.message("samebug.component.helpRequest.ask.error.unhandled"));
    }

    @Override
    protected void showFieldErrors(List<FieldError> fieldErrors) throws FormMismatchException {
        form.failRequestTip(fieldErrors);
    }

    @Override
    protected void showGlobalErrors(List<String> globalErrors) {
        if (!globalErrors.isEmpty()) frame.popupError(globalErrors.get(0));
    }
}
