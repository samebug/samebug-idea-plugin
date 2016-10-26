/**
 * Copyright 2016 Samebug, Inc.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
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
import com.intellij.util.messages.MessageBusConnection;
import com.samebug.clients.idea.notification.SamebugNotifications;
import com.samebug.clients.idea.tracking.Events;
import com.samebug.clients.idea.ui.SettingsDialog;
import com.samebug.clients.search.api.WebUrlBuilder;
import com.samebug.clients.search.api.entities.UserInfo;
import com.samebug.clients.search.api.exceptions.SamebugClientException;
import com.samebug.clients.search.api.exceptions.UnknownApiKey;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.atomic.AtomicReference;


@State(
        name = "SamebugConfiguration",
        storages = {
                @Storage(id = "SamebugClient", file = "$APP_CONFIG$/SamebugClient.xml")
        }
)
final public class IdeaSamebugPlugin implements ApplicationComponent, PersistentStateComponent<ApplicationSettings> {
    final private static Logger LOGGER = Logger.getInstance(IdeaSamebugPlugin.class);
    private AtomicReference<ApplicationSettings> state = new AtomicReference<ApplicationSettings>(new ApplicationSettings());

    final ClientService client = ApplicationManager.getApplication().getComponent(ClientService.class);

    {
        client.configure(state.get().getNetworkConfig());
    }

    private WebUrlBuilder urlBuilder = new WebUrlBuilder(state.get().serverRoot);

    @Nullable
    private TimedTasks timedTasks;

    @Nullable
    private MessageBusConnection connection;

    // TODO Unlike other methods, this one executes the http request on the caller thread. Is it ok?
    public void setApiKey(@NotNull String apiKey) throws SamebugClientException, UnknownApiKey {
        ApplicationSettings currentState = state.get();
        UserInfo userInfo = null;
        currentState.apiKey = apiKey;
        client.configure(currentState.getNetworkConfig());
        userInfo = client.getUserInfo(apiKey);
        if (!userInfo.getUserExist()) {
            throw new UnknownApiKey(apiKey);
        } else {
            currentState.userId = userInfo.getUserId();
            currentState.avatarUrl = userInfo.getAvatarUrl().toString();
            currentState.workspaceId = userInfo.getDefaultWorkspaceId();
            saveSettings(currentState);
        }
    }

    public void saveSettings(final ApplicationSettings settings) {
        ApplicationSettings newSettings = new ApplicationSettings(settings);
        state.set(newSettings);
        try {
            client.configure(newSettings.getNetworkConfig());
            urlBuilder = new WebUrlBuilder(newSettings.serverRoot);
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
    public ClientService getClient() {
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
                ApplicationSettings newSettings = new ApplicationSettings(state.get());
                if (newSettings.apiKey == null) {
                    SettingsDialog.setup(null);
                } else {
                    try {
                        UserInfo userInfo = client.getUserInfo(newSettings.apiKey);
                        if (userInfo.getUserExist()) {
                            newSettings.userId = userInfo.getUserId();
                            newSettings.avatarUrl = userInfo.getAvatarUrl().toString();
                            saveSettings(newSettings);
                        }
                    } catch (SamebugClientException e) {
                        LOGGER.warn("Failed to get user info", e);
                    }
                }
            }
        });

        connection = ApplicationManager.getApplication().getMessageBus().connect();
        timedTasks = new TimedTasks(connection);
    }

    @Override
    public void disposeComponent() {
        if (connection != null) {
            connection.disconnect();
        }
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
        return new ApplicationSettings(state.get());
    }

    @Override
    public void loadState(ApplicationSettings state) {
        ApplicationSettings newSettings = new ApplicationSettings(state);
        this.state.set(newSettings);
        client.configure(newSettings.getNetworkConfig());
        urlBuilder = new WebUrlBuilder(newSettings.serverRoot);
    }
}
