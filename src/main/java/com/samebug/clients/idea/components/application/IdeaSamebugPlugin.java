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
import com.samebug.clients.search.api.entities.UserInfo;
import com.samebug.clients.search.api.exceptions.SamebugClientException;
import com.samebug.clients.search.api.exceptions.UnknownApiKey;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


@State(
        name = "SamebugConfiguration",
        storages = {
                @Storage(id = "SamebugClient", file = "$APP_CONFIG$/SamebugClient.xml")
        }
)
final public class IdeaSamebugPlugin implements ApplicationComponent, PersistentStateComponent<ApplicationSettings> {
    private IdeaClientService client = new IdeaClientService(null);

    // TODO Unlike other methods, this one executes the http request on the caller thread. Is it ok?
    public void setApiKey(@NotNull String apiKey) throws SamebugClientException, UnknownApiKey {
        UserInfo userInfo = null;
        try {
            client = new IdeaClientService(apiKey);
            state.setApiKey(apiKey);
            userInfo = client.getUserInfo(apiKey);
            if (!userInfo.isUserExist) {
                throw new UnknownApiKey(apiKey);
            } else {
                state.setUserId(userInfo.userId);
            }
        } finally {
            Tracking.appTracking().trace(Events.apiKeySet());
        }
    }

    @Nullable
    public String getApiKey() {
        return state.getApiKey();
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

    // ApplicationComponent overrides
    @Override
    public void initComponent() {
        SamebugNotifications.registerNotificationGroups();
        ApplicationManager.getApplication().executeOnPooledThread(new Runnable() {
            @Override
            public void run() {
                if (state.getApiKey() == null) {
                    SettingsDialog.setup(state.getApiKey());
                } else {
                    try {
                        UserInfo userInfo = client.getUserInfo(state.getApiKey());
                        if (userInfo.isUserExist) state.setUserId(userInfo.userId);
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
    @Nullable
    @Override
    public ApplicationSettings getState() {
        return this.state;
    }

    @Override
    public void loadState(ApplicationSettings state) {
        this.state = state;
        client = new IdeaClientService(state.getApiKey());
    }

    private ApplicationSettings state = new ApplicationSettings();
    final private static Logger LOGGER = Logger.getInstance(IdeaSamebugPlugin.class);

}
