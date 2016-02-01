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
package com.samebug.clients.idea;

import com.android.ddmlib.AndroidDebugBridge;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.ApplicationComponent;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.samebug.clients.exceptions.UnknownApiKey;
import com.samebug.clients.idea.intellij.autosearch.StackTraceSearch;
import com.samebug.clients.idea.intellij.notification.SamebugNotification;
import com.samebug.clients.idea.intellij.settings.SettingsDialog;
import com.samebug.clients.rest.SamebugClient;
import com.samebug.clients.rest.entities.UserInfo;
import com.samebug.clients.rest.exceptions.SamebugClientException;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.net.URI;

@State(
        name = "SamebugConfiguration",
        storages = {
                @Storage(id = "SamebugClient", file = "$APP_CONFIG$/SamebugClient.xml")
        }
)
public class SamebugIdeaPlugin implements ApplicationComponent, PersistentStateComponent<SamebugState> {

    private SamebugIdeaPlugin() {
        SamebugNotification.registerNotificationGroups();
        this.client = new SamebugClient(this, URI.create("https://samebug.io/"));
        this.stackTraceSearch = new StackTraceSearch(client);


    }

    @NotNull
    public static SamebugIdeaPlugin getInstance() {
        SamebugIdeaPlugin instance = ApplicationManager.getApplication().getComponent(SamebugIdeaPlugin.class);
        if (instance == null) {
            throw new Error("No Samebug IDEA plugin available");
        } else {
            return instance;
        }
    }

    public static void initIfNeeded() {
        final SamebugIdeaPlugin plugin = SamebugIdeaPlugin.getInstance();
        if (!isInitialized()) SettingsDialog.setup(plugin);
    }

    @NotNull
    public static SamebugClient getClient() {
        return getInstance().client;
    }

    @Nullable
    public static StackTraceSearch getStackTraceSearch() {
        return getInstance().stackTraceSearch;
    }

    public static boolean isInitialized() {
        SamebugState state = getInstance().getState();
        return state.getApiKey() != null &&  state.getUserId() == null;
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
    public SamebugState getState() {
        return this.state;
    }

    @Override
    public void loadState(SamebugState state) {
        this.state = state;
    }

    @Nullable
    public String getApiKey() {
        return state.getApiKey();
    }

    public void setApiKey(String apiKey) throws SamebugClientException, UnknownApiKey {
        UserInfo userInfo = client.checkApiKey(apiKey);
        if (!userInfo.isUserExist) {
            throw new UnknownApiKey(apiKey);
        }
        state.setApiKey(apiKey);
        state.setUserId(userInfo.userId);
        state.setUserDisplayName(userInfo.displayName);

    }

    private final StackTraceSearch stackTraceSearch;
    private final SamebugClient client;
    private SamebugState state = new SamebugState();
}
