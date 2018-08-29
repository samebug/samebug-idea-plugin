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
import com.samebug.clients.common.services.AuthenticationService;
import com.samebug.clients.common.ui.component.authentication.ILogInForm;
import com.samebug.clients.common.ui.frame.IFrame;
import com.samebug.clients.common.ui.modules.MessageService;
import com.samebug.clients.http.entities.authentication.AuthenticationResponse;
import com.samebug.clients.http.exceptions.SamebugClientException;
import com.samebug.clients.http.form.LogIn;
import com.samebug.clients.idea.components.application.IdeaSamebugPlugin;
import org.jetbrains.annotations.NotNull;

public abstract class LogInFormHandler extends PostFormHandler<AuthenticationResponse, LogIn.BadRequest> {
    private static final Logger LOGGER = Logger.getInstance(LogInFormHandler.class);
    final IFrame frame;
    final ILogInForm form;
    final LogIn.Data data;

    public LogInFormHandler(IFrame frame, ILogInForm form, LogIn.Data data) {
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
    protected AuthenticationResponse postForm() throws SamebugClientException, LogIn.BadRequest {
        final AuthenticationService authenticationService = IdeaSamebugPlugin.getInstance().authenticationService;
        return authenticationService.logIn(data);
    }

    @Override
    protected void handleBadRequest(@NotNull LogIn.BadRequest fieldErrors) {
        ILogInForm.BadRequest.Email email = null;
        ILogInForm.BadRequest.Password password = null;
        for (LogIn.ErrorCode errorCode : fieldErrors.errorList.getErrorCodes()) {
            switch (errorCode) {
                case INVALID_CREDENTIALS:
                    email = ILogInForm.BadRequest.Email.UNKNOWN_CREDENTIALS;
                    password = ILogInForm.BadRequest.Password.UNKNOWN_CREDENTIALS;
                    break;
                default:
                    LOGGER.warn("Unhandled error code " + errorCode);
            }
        }
        ILogInForm.BadRequest b = new ILogInForm.BadRequest(email, password);
        form.failPost(b);
    }

    @Override
    protected void handleOtherClientExceptions(@NotNull SamebugClientException exception) {
        LOGGER.warn("Unhandled samebug client exception", exception);
        frame.popupError(MessageService.message("samebug.component.authentication.logIn.error.unhandled"));
        form.failPost(null);
    }
}
