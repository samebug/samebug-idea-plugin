/**
 * Copyright 2016 Samebug, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.samebug.clients.idea.components.application;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.ApplicationComponent;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.diagnostic.Logger;
import com.samebug.clients.idea.notification.SamebugNotifications;
import com.samebug.clients.idea.tracking.Events;
import com.samebug.clients.idea.ui.SettingsDialog;
import com.samebug.clients.search.api.WebUrlBuilder;
import com.samebug.clients.search.api.entities.UserInfo;
import com.samebug.clients.search.api.exceptions.SamebugClientException;
import com.samebug.clients.search.api.exceptions.UnknownApiKey;
import org.jetbrains.annotations.NotNull;


@State(
        name = "SamebugConfiguration",
        storages = {
                @Storage(id = "SamebugClient", file = "$APP_CONFIG$/SamebugClient.xml")
        }
)
final public class IdeaSamebugPlugin implements ApplicationComponent, PersistentStateComponent<ApplicationSettings> {
    final private static Logger LOGGER = Logger.getInstance(IdeaSamebugPlugin.class);
    private ApplicationSettings state = new ApplicationSettings();

    private IdeaClientService client = new IdeaClientService(state.getNetworkConfig());
    {
        ApplicationManager.getApplication().getComponent(ClientService.class).configure(state.getNetworkConfig());
    }
    private WebUrlBuilder urlBuilder = new WebUrlBuilder(state.serverRoot);

    // TODO Unlike other methods, this one executes the http request on the caller thread. Is it ok?
    public void setApiKey(@NotNull String apiKey) throws SamebugClientException, UnknownApiKey {
        UserInfo userInfo = null;
        state.apiKey = apiKey;
        client = new IdeaClientService(state.getNetworkConfig());
        urlBuilder = new WebUrlBuilder(state.serverRoot);
        userInfo = client.getUserInfo(apiKey);
        if (!userInfo.isUserExist) {
            throw new UnknownApiKey(apiKey);
        } else {
            state.userId = userInfo.userId;
            state.avatarUrl = userInfo.avatarUrl.toString();
            state.workspaceId = userInfo.defaultWorkspaceId;
            saveSettings(state);
        }
    }

    public void saveSettings(final ApplicationSettings settings) {
        state = new ApplicationSettings(settings);
        try {
            client = new IdeaClientService(state.getNetworkConfig());
            urlBuilder = new WebUrlBuilder(state.serverRoot);
        } finally {
            Tracking.appTracking().trace(Events.apiKeySet());
        }
    }

    @NotNull
    public static IdeaSamebugPlugin getInstance() {
        IdeaSamebugPlugin instance = ApplicationManager.getApplication().getComponent(IdeaSamebugPlugin.class);
        if (instance == null) {
            throw new Error("No Samebug IDEA plugin available");
        } else {
            return instance;
        }
    }

    @NotNull
    public IdeaClientService getClient() {
        return client;
    }

    @NotNull
    public WebUrlBuilder getUrlBuilder() {
        return urlBuilder;
    }

    // ApplicationComponent overrides
    @Override
    public void initComponent() {
        SamebugNotifications.registerNotificationGroups();
        ApplicationManager.getApplication().executeOnPooledThread(new Runnable() {
            @Override
            public void run() {
                if (state.apiKey == null) {
                    SettingsDialog.setup(null);
                } else {
                    try {
                        UserInfo userInfo = client.getUserInfo(state.apiKey);
                        if (userInfo.isUserExist) {
                            state.userId = userInfo.userId;
                            state.avatarUrl = userInfo.avatarUrl.toString();
                        }
                    } catch (SamebugClientException e) {
                        LOGGER.warn("Failed to get user info", e);
                    }
                }
            }
        });
    }

    @Override
    public void disposeComponent() {
    }

    @Override
    @NotNull
    public String getComponentName() {
        return getClass().getSimpleName();
    }

    // PersistentStateComponent overrides
    @NotNull
    @Override
    public ApplicationSettings getState() {
        return this.state;
    }

    @Override
    public void loadState(ApplicationSettings state) {
        this.state = state;
        client = new IdeaClientService(state.getNetworkConfig());
    }
}
