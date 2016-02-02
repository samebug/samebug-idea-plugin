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
package com.samebug.clients.idea.application;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.ApplicationComponent;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.samebug.clients.search.api.exceptions.UnknownApiKey;
import com.samebug.clients.idea.notification.SamebugNotification;
import com.samebug.clients.search.api.SamebugClient;
import com.samebug.clients.search.api.entities.UserInfo;
import com.samebug.clients.search.api.exceptions.SamebugClientException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.URI;

@State(
        name = "SamebugConfiguration",
        storages = {
                @Storage(id = "SamebugClient", file = "$APP_CONFIG$/SamebugClient.xml")
        }
)
public class IdeaSamebugPlugin implements ApplicationComponent, PersistentStateComponent<SamebugSettings> {

    private IdeaSamebugPlugin() {
        SamebugNotification.registerNotificationGroups();
        this.client = new IdeaSamebugClient(this, URI.create("https://samebug.io/"));
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

    public static void initIfNeeded() {
        final IdeaSamebugPlugin plugin = IdeaSamebugPlugin.getInstance();
        if (!isInitialized(plugin)) SettingsDialog.setup(plugin);
    }

    @NotNull
    public static SamebugClient getClient() {
        return getInstance().client;
    }

    public static boolean isInitialized(IdeaSamebugPlugin plugin) {
        SamebugSettings state = plugin.getState();
        return state.isInitialized();
    }

    @Override
    public void initComponent() {
    }


    @Override
    public void disposeComponent() {
    }

    @Override
    @NotNull
    public String getComponentName() {
        return getClass().getSimpleName();
    }

    @NotNull
    @Override
    public SamebugSettings getState() {
        return this.state;
    }

    @Override
    public void loadState(SamebugSettings state) {
        this.state = state;
    }

    @Nullable
    public String getApiKey() {
        return state.getApiKey();
    }

    public void setApiKey(String apiKey) throws SamebugClientException, UnknownApiKey {
        UserInfo userInfo = client.getUserInfo(apiKey);
        state.setApiKey(apiKey);
        state.setUserId(userInfo.userId);
        state.setUserDisplayName(userInfo.displayName);

    }

    private final SamebugClient client;
    private SamebugSettings state = new SamebugSettings();
}
