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

import com.samebug.clients.http.response.RestError;
import com.samebug.clients.http.entities.solution.RestHit;
import com.samebug.clients.http.entities.solution.Tip;
import com.samebug.clients.http.exceptions.SamebugClientException;
import com.samebug.clients.http.form.CreateTip;
import com.samebug.clients.http.form.FieldError;
import com.samebug.clients.common.services.SolutionService;
import com.samebug.clients.common.ui.component.community.IHelpOthersCTA;
import com.samebug.clients.common.ui.component.form.FormMismatchException;
import com.samebug.clients.common.ui.frame.IFrame;
import com.samebug.clients.idea.components.application.IdeaSamebugPlugin;
import com.samebug.clients.swing.ui.modules.MessageService;

import java.util.List;

public abstract class CreateTipFormHandler extends PostFormHandler<RestHit<Tip>> {
    final IFrame frame;
    final IHelpOthersCTA form;
    final CreateTip data;

    public CreateTipFormHandler(IFrame frame, IHelpOthersCTA form, CreateTip data) {
        this.frame = frame;
        this.form = form;
        this.data = data;
    }

    @Override
    protected void beforePostForm() {
        form.startPostTip();
    }

    @Override
    protected RestHit<Tip> postForm() throws SamebugClientException {
        final SolutionService solutionService = IdeaSamebugPlugin.getInstance().solutionService;
        return solutionService.postTip(data.searchId, data.body, data.sourceUrl, data.helpRequestId);
    }

    @Override
    protected void handleFieldError(FieldError fieldError, List<String> globalErrors, List<FieldError> fieldErrors) {
        super.handleFieldError(fieldError, globalErrors, fieldErrors);
        if (CreateTip.BODY.equals(fieldError.key)) fieldErrors.add(fieldError);
        else globalErrors.add(MessageService.message("samebug.error.pluginBug"));
    }

    @Override
    protected void handleNonFormBadRequests(RestError nonFormError, List<String> globalErrors, List<FieldError> fieldErrors) {
        super.handleNonFormBadRequests(nonFormError, globalErrors, fieldErrors);
        if (nonFormError.code.equals(CreateTip.E_TOO_SHORT)) fieldErrors.add(new FieldError(CreateTip.BODY, CreateTip.E_TOO_SHORT));
        else if (nonFormError.code.equals(CreateTip.E_TOO_LONG)) fieldErrors.add(new FieldError(CreateTip.BODY, CreateTip.E_TOO_LONG));
        else globalErrors.add(MessageService.message("samebug.component.tip.write.error.badRequest"));
    }

    @Override
    protected void handleOtherClientExceptions(SamebugClientException exception, List<String> globalErrors, List<FieldError> fieldErrors) {
        super.handleOtherClientExceptions(exception, globalErrors, fieldErrors);
        globalErrors.add(MessageService.message("samebug.component.tip.write.error.unhandled"));
    }

    @Override
    protected void showFieldErrors(List<FieldError> fieldErrors) throws FormMismatchException {
        form.failPostTipWithFormError(fieldErrors);
    }

    @Override
    protected void showGlobalErrors(List<String> globalErrors) {
        // TODO showing more errors?
        if (!globalErrors.isEmpty()) frame.popupError(globalErrors.get(0));
    }
}
