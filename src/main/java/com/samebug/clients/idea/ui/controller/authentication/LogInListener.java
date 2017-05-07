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

import com.samebug.clients.common.ui.component.authentication.ILogInForm;
import com.samebug.clients.http.entities.authentication.AuthenticationResponse;
import com.samebug.clients.http.entities.user.Me;
import com.samebug.clients.http.form.LogIn;
import com.samebug.clients.idea.components.application.IdeaSamebugPlugin;
import com.samebug.clients.idea.tracking.Events;
import com.samebug.clients.idea.ui.controller.form.LogInFormHandler;
import com.samebug.clients.idea.ui.modules.BrowserUtil;
import com.samebug.clients.swing.ui.modules.TrackingService;

import java.net.URI;

public final class LogInListener implements ILogInForm.Listener {
    final AuthenticationController controller;

    public LogInListener(AuthenticationController controller) {
        this.controller = controller;
    }

    @Override
    public void logIn(final ILogInForm source, String email, String password) {
        new LogInFormHandler(controller.view, source, new LogIn.Data(email, password)) {
            @Override
            protected void afterPostForm(AuthenticationResponse response) {
                source.successPost();
                controller.twc.focusOnHelpRequestList();
                TrackingService.trace(Events.registrationLogInSucceeded());
            }
        }.execute();
    }

    @Override
    public void forgotPassword(ILogInForm source) {
        IdeaSamebugPlugin plugin = IdeaSamebugPlugin.getInstance();
        Me user = plugin.profileStore.getUser();
        // TODO, well, probably we will not know the user id if he is not logged in...
        if (user != null) {
            int myUserId = user.getId();
            URI forgottenPasswordUri = plugin.uriBuilder.forgottenPassword(myUserId);
            BrowserUtil.browse(forgottenPasswordUri);
        }
    }
}
