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
import com.samebug.clients.common.services.AuthenticationService;
import com.samebug.clients.common.ui.component.authentication.ISignUpForm;
import com.samebug.clients.common.ui.frame.IFrame;
import com.samebug.clients.common.ui.modules.MessageService;
import com.samebug.clients.http.entities.authentication.AuthenticationResponse;
import com.samebug.clients.http.exceptions.SamebugClientException;
import com.samebug.clients.http.form.SignUp;
import com.samebug.clients.idea.components.application.IdeaSamebugPlugin;
import org.jetbrains.annotations.NotNull;

public abstract class SignUpFormHandler extends PostFormHandler<AuthenticationResponse, SignUp.BadRequest> {
    private static final Logger LOGGER = Logger.getInstance(SignUpFormHandler.class);
    final IFrame frame;
    final ISignUpForm form;
    final SignUp.Data data;

    public SignUpFormHandler(IFrame frame, ISignUpForm form, SignUp.Data data) {
        this.frame = frame;
        this.form = form;
        this.data = data;
    }

    @Override
    protected void beforePostForm() {
        form.startPost();
    }

    @NotNull
    @Override
    protected AuthenticationResponse postForm() throws SamebugClientException, SignUp.BadRequest {
        final AuthenticationService authenticationService = IdeaSamebugPlugin.getInstance().authenticationService;
        return authenticationService.signUp(data);
    }

    @Override
    protected void handleBadRequest(@NotNull SignUp.BadRequest fieldErrors) {
        ISignUpForm.BadRequest.DisplayName displayName = null;
        ISignUpForm.BadRequest.Email email = null;
        ISignUpForm.BadRequest.Password password = null;
        for (SignUp.ErrorCode errorCode : fieldErrors.errorList.getErrorCodes()) {
            switch (errorCode) {
                case EMAIL_USED:
                    email = ISignUpForm.BadRequest.Email.TAKEN;
                    break;
                case EMAIL_INVALID:
                    email = ISignUpForm.BadRequest.Email.INVALID;
                    break;
                case EMAIL_LONG:
                    email = ISignUpForm.BadRequest.Email.LONG;
                    break;
                case DISPLAYNAME_LONG:
                    displayName = ISignUpForm.BadRequest.DisplayName.TOO_LONG;
                    break;
                case DISPLAYNAME_EMPTY:
                    displayName = ISignUpForm.BadRequest.DisplayName.EMPTY;
                    break;
                case PASSWORD_EMPTY:
                    password = ISignUpForm.BadRequest.Password.SHORT;
                    break;
                default:
                    LOGGER.warn("Unhandled error code " + errorCode);
            }
        }
        if (displayName != null || email != null || password != null) {
            ISignUpForm.BadRequest b = new ISignUpForm.BadRequest(displayName, email, password);
            form.failPost(b);
        } else {
            frame.popupError(MessageService.message("samebug.component.authentication.signUp.error.unhandled"));
            form.failPost(null);
        }
    }

    @Override
    protected void handleOtherClientExceptions(@NotNull SamebugClientException exception) {
        frame.popupError(MessageService.message("samebug.component.authentication.signUp.error.unhandled"));
        form.failPost(null);
    }
}
