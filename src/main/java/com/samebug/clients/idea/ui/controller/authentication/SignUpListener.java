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
package com.samebug.clients.idea.ui.controller.authentication;

import com.samebug.clients.common.ui.component.authentication.ISignUpForm;
import com.samebug.clients.http.entities2.authentication.AuthenticationResponse;
import com.samebug.clients.http.form.SignUp;
import com.samebug.clients.idea.tracking.Events;
import com.samebug.clients.idea.ui.controller.form.SignUpFormHandler;
import com.samebug.clients.swing.ui.modules.TrackingService;

public final class SignUpListener implements ISignUpForm.Listener {
    final AuthenticationController controller;

    public SignUpListener(AuthenticationController controller) {
        this.controller = controller;
    }

    @Override
    public void signUp(final ISignUpForm source, String displayName, String email, String password) {
        new SignUpFormHandler(controller.view, source, new SignUp.Data(displayName, email, password)) {
            @Override
            protected void afterPostForm(AuthenticationResponse response) {
                source.successPost();
                controller.twc.focusOnHelpRequestList();
                TrackingService.trace(Events.registrationSignUpSucceeded("credentials"));
            }

        }.execute();
    }
}

