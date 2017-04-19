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

import com.samebug.clients.common.api.client.OAuthServer;
import com.samebug.clients.common.api.entities.profile.LoggedInUser;
import com.samebug.clients.common.api.exceptions.SamebugClientException;
import com.samebug.clients.common.api.form.LogIn;
import com.samebug.clients.common.services.AuthenticationService;
import com.samebug.clients.common.ui.component.authentication.ILogInForm;
import com.samebug.clients.idea.components.application.IdeaSamebugPlugin;
import com.samebug.clients.idea.tracking.Events;
import com.samebug.clients.idea.ui.controller.form.LogInFormHandler;
import com.samebug.clients.idea.ui.modules.BrowserUtil;
import com.samebug.clients.swing.ui.modules.TrackingService;

import java.net.URL;
import java.text.MessageFormat;

public final class LogInListener implements ILogInForm.Listener {
    final AuthenticationController controller;

    public LogInListener(AuthenticationController controller) {
        this.controller = controller;
    }

    @Override
    public void logIn(final ILogInForm source, String email, String password) {
        new LogInFormHandler(controller.view, source, new LogIn(email, password)) {
            @Override
            protected void afterPostForm(LoggedInUser response) {
                source.successPost();
                controller.twc.focusOnHelpRequestList();
                TrackingService.trace(Events.registrationLogInSucceeded());
            }
        }.execute();
    }

    @Override
    public void forgotPassword(ILogInForm source) {
        URL forgottenPasswordUrl = IdeaSamebugPlugin.getInstance().urlBuilder.forgottenPassword();
        BrowserUtil.browse(forgottenPasswordUrl);
    }

    @Override
    public void facebookLogin() {
    }

    @Override
    public void googleLogin() {
        OAuthServer s = new OAuthServer(new OAuthServer.Listener() {
            @Override
            public void success(String code) {
                System.out.println("Got the code: " + code);
                // TODO log in via authenticationservice
                final AuthenticationService authenticationService = IdeaSamebugPlugin.getInstance().authenticationService;
                try {
                    authenticationService.logIn(new LogIn("daniel.poroszkai@samebug.io", "samebug_demo"));
                } catch (SamebugClientException e) {
                    e.printStackTrace();
                }
                controller.twc.focusOnHelpRequestList();

            }

            @Override
            public void fail() {
                System.out.println("Authentication failed!");
                // TODO popup error
            }
        });

        String scope = "email profile";
        String redirectUri = "http://127.0.0.1:9990";
        String clientId = "1086819285889-h3u7vm9vletsprtbv2uref3t6jjiq4vv.apps.googleusercontent.com";
        String url = MessageFormat.format("https://accounts.google.com/o/oauth2/v2/auth?" +
                "scope={0}&" +
                "response_type=code&" +
                "state=security_token%3D138r5719ru3e1%26url%3Dhttps://oauth2.example.com/token&" +
                "redirect_uri={1}&" +
                "client_id={2}", scope, redirectUri, clientId);
        com.intellij.ide.BrowserUtil.browse(url);

    }
}
