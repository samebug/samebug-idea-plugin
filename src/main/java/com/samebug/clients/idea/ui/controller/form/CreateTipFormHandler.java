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

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.samebug.clients.common.api.client.FormRestError;
import com.samebug.clients.common.api.client.RestError;
import com.samebug.clients.common.api.entities.solution.RestHit;
import com.samebug.clients.common.api.entities.solution.Tip;
import com.samebug.clients.common.api.exceptions.BadRequest;
import com.samebug.clients.common.api.exceptions.SamebugClientException;
import com.samebug.clients.common.api.form.CreateTip;
import com.samebug.clients.common.api.form.FieldError;
import com.samebug.clients.common.services.SolutionService;
import com.samebug.clients.common.ui.component.community.IHelpOthersCTA;
import com.samebug.clients.common.ui.component.form.FormMismatchException;
import com.samebug.clients.common.ui.frame.IFrame;
import com.samebug.clients.idea.components.application.IdeaSamebugPlugin;
import com.samebug.clients.swing.ui.modules.MessageService;

import java.util.ArrayList;
import java.util.List;

// TODO extract to generic forms
public abstract class CreateTipFormHandler {
    final static Logger LOGGER = Logger.getInstance(CreateTipFormHandler.class);

    final IFrame frame;
    final IHelpOthersCTA form;
    final CreateTip data;

    public CreateTipFormHandler(IFrame frame, IHelpOthersCTA form, CreateTip data) {
        this.frame = frame;
        this.form = form;
        this.data = data;
    }

    public final void execute() {
        beforePostForm();
        ApplicationManager.getApplication().executeOnPooledThread(new Runnable() {
            @Override
            public void run() {
                try {
                    final RestHit<Tip> response = postForm();
                    ApplicationManager.getApplication().invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            form.successPostTip();
                            afterPostForm(response);
                        }
                    });
                } catch (SamebugClientException e) {
                    handleException(e);
                }
            }
        });
    }

    protected void beforePostForm() {
        form.startPostTip();
    }

    protected RestHit<Tip> postForm() throws SamebugClientException {
        final SolutionService solutionService = IdeaSamebugPlugin.getInstance().solutionService;
        return solutionService.postTip(data.searchId, data.body, data.sourceUrl, data.helpRequestId);
    }

    protected void handleFieldError(FieldError fieldError, List<String> globalErrors, List<FieldError> fieldErrors) {
        if (CreateTip.BODY.equals(fieldError.key)) fieldErrors.add(fieldError);
        else {
            LOGGER.warn("Unhandled form error: " + fieldError);
            globalErrors.add(MessageService.message("samebug.error.pluginBug"));
        }
    }

    protected void handleNonFormBadRequests(RestError nonFormError, List<String> globalErrors, List<FieldError> fieldErrors) {
        LOGGER.warn("Unhandled bad request: " + nonFormError);
        globalErrors.add(MessageService.message("samebug.component.tip.write.error.badRequest"));
    }

    protected void handleOtherClientExceptions(SamebugClientException exception, List<String> globalErrors, List<FieldError> fieldErrors) {
        LOGGER.warn("Failed to post tip", exception);
        globalErrors.add(MessageService.message("samebug.component.tip.write.error.unhandled"));
    }

    protected void showFieldErrors(List<FieldError> fieldErrors) throws FormMismatchException {
        form.failPostTipWithFormError(fieldErrors);
    }

    protected void showGlobalErrors(List<String> globalErrors) {
        // TODO showing more errors?
        if (!globalErrors.isEmpty()) frame.popupError(globalErrors.get(0));
    }

    private void handleException(SamebugClientException exception) {
        final List<String> globalErrors = new ArrayList<String>();
        final List<FieldError> fieldErrors = new ArrayList<FieldError>();

        if (exception instanceof BadRequest) {
            if (((BadRequest) exception).getRestError() instanceof FormRestError) {
                FormRestError formError = (FormRestError) ((BadRequest) exception).getRestError();
                for (FieldError fieldError : formError.getAllFieldErrors()) handleFieldError(fieldError, globalErrors, fieldErrors);
            } else {
                RestError otherError = (RestError) ((BadRequest) exception).getRestError();
                handleNonFormBadRequests(otherError, globalErrors, fieldErrors);
            }
        } else {
            handleOtherClientExceptions(exception, globalErrors, fieldErrors);
        }

        ApplicationManager.getApplication().invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    showFieldErrors(fieldErrors);
                } catch (FormMismatchException formException) {
                    LOGGER.warn("Unprocessed form errors", formException);
                    globalErrors.add(MessageService.message("samebug.error.pluginBug"));
                }

                showGlobalErrors(globalErrors);
            }
        });
    }


    protected abstract void afterPostForm(RestHit<Tip> response);
}
