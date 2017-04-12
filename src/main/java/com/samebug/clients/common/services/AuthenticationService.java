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
package com.samebug.clients.common.services;

import com.samebug.clients.http.client.SamebugClient;
import com.samebug.clients.http.entities.profile.LoggedInUser;
import com.samebug.clients.http.entities.profile.UserInfo;
import com.samebug.clients.http.exceptions.SamebugClientException;
import com.samebug.clients.http.exceptions.SamebugException;
import com.samebug.clients.http.form.LogIn;
import com.samebug.clients.http.form.SignUp;
import com.samebug.clients.idea.components.application.ApplicationSettings;
import com.samebug.clients.idea.components.application.IdeaSamebugPlugin;
import org.jetbrains.annotations.Nullable;

public final class AuthenticationService {
    final SamebugClient client;

    public AuthenticationService(SamebugClient client) {
        this.client = client;
    }

    public UserInfo apiKeyAuthentication(final String apiKey, @Nullable final Integer workspaceId) throws SamebugException {
        return client.getUserInfo(apiKey);
        // TODO if workspaceId is null, save the returned default workspace id to application settings.
    }


    public LoggedInUser logIn(final LogIn data) throws SamebugClientException, LogIn.BadRequest {
        LoggedInUser result = client.logIn(data);
        updateSettings(result);
        return result;
    }

    public LoggedInUser signUp(final SignUp data) throws SamebugClientException, SignUp.BadRequest {
        LoggedInUser result = client.signUp(data);
        updateSettings(result);
        return result;
    }

    public LoggedInUser anonymousUse() throws SamebugClientException {
        LoggedInUser result = client.anonymousUse();
        updateSettings(result);
        return result;
    }

    private void updateSettings(LoggedInUser user) {
        IdeaSamebugPlugin plugin = IdeaSamebugPlugin.getInstance();
        ApplicationSettings oldSettings = plugin.getState();
        ApplicationSettings newSettings = new ApplicationSettings(oldSettings);
        newSettings.apiKey = user.apiKey;
        if (oldSettings.workspaceId == null) newSettings.workspaceId = user.defaultWorkspaceId;
        newSettings.userId = user.userId;
        if (!oldSettings.equals(newSettings)) plugin.saveSettings(newSettings);
    }
}
