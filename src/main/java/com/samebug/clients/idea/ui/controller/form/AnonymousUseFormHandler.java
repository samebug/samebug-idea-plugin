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

import com.samebug.clients.common.services.AuthenticationService;
import com.samebug.clients.common.ui.component.authentication.IAnonymousUseForm;
import com.samebug.clients.common.ui.frame.IFrame;
import com.samebug.clients.common.ui.modules.MessageService;
import com.samebug.clients.http.entities.authentication.AuthenticationResponse;
import com.samebug.clients.http.exceptions.FormException;
import com.samebug.clients.http.exceptions.SamebugClientException;
import com.samebug.clients.idea.components.application.IdeaSamebugPlugin;
import org.jetbrains.annotations.NotNull;

public abstract class AnonymousUseFormHandler extends PostFormHandler<AuthenticationResponse, FormException> {
    final IFrame frame;
    final IAnonymousUseForm form;

    public AnonymousUseFormHandler(IFrame frame, IAnonymousUseForm form) {
        this.frame = frame;
        this.form = form;
    }

    @Override
    protected void beforePostForm() {
        form.startPost();
    }

    @NotNull
    @Override
    protected AuthenticationResponse postForm() throws SamebugClientException {
        final AuthenticationService authenticationService = IdeaSamebugPlugin.getInstance().authenticationService;
        return authenticationService.anonymousUse();
    }

    @Override
    protected void handleBadRequest(@NotNull FormException fieldErrors) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void handleOtherClientExceptions(@NotNull SamebugClientException exception) {
        frame.popupError(MessageService.message("samebug.component.authentication.anonymousUse.error.unhandled"));
    }
}
