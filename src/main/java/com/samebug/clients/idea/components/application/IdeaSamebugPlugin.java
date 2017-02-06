/**
 * Copyright 2017 Samebug, Inc.
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

import com.intellij.notification.NotificationDisplayType;
import com.intellij.notification.impl.NotificationsConfigurationImpl;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.ApplicationComponent;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.util.messages.MessageBus;
import com.intellij.util.messages.MessageBusConnection;
import com.samebug.clients.common.search.api.WebUrlBuilder;
import com.samebug.clients.common.search.api.entities.UserInfo;
import com.samebug.clients.common.search.api.exceptions.SamebugClientException;
import com.samebug.clients.common.search.api.exceptions.UnknownApiKey;
import com.samebug.clients.common.services.ClientService;
import com.samebug.clients.common.services.HistoryService;
import com.samebug.clients.common.services.ProfileService;
import com.samebug.clients.common.services.SolutionService;
import com.samebug.clients.idea.notification.SamebugNotifications;
import com.samebug.clients.idea.tracking.Events;
import com.samebug.clients.idea.ui.SettingsDialog;
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

    private WebUrlBuilder urlBuilder = new WebUrlBuilder(state.get().serverRoot);

    @Nullable
    private ClientService clientService;
    @Nullable
    private HistoryService historyService;
    @Nullable
    private ProfileService profileService;
    @Nullable
    private SolutionService solutionService;

    @Nullable
    private TimedTasks timedTasks;

    @Nullable
    private MessageBusConnection connection;

    // NOTE should not be called from UI thread
    public void setApiKey(@NotNull String apiKey) throws SamebugClientException, UnknownApiKey {
        ApplicationSettings currentState = state.get();
        UserInfo userInfo = null;
        currentState.apiKey = apiKey;
        clientService.configure(currentState.getNetworkConfig());
        userInfo = profileService.loadUserInfo(apiKey);
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
            clientService.configure(newSettings.getNetworkConfig());
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
    public WebUrlBuilder getUrlBuilder() {
        return urlBuilder;
    }

    @Nullable
    public ClientService getClient() {
        return clientService;
    }

    @Nullable
    public ProfileService getProfileService() {
        return profileService;
    }

    @Nullable
    public SolutionService getSolutionService() {
        return solutionService;
    }

    @Nullable
    public HistoryService getHistoryService() {
        return historyService;
    }

    // ApplicationComponent overrides
    @Override
    public void initComponent() {
        SamebugNotifications.registerNotificationGroups();
        if (!state.get().wereNotificationsDisabled) {
            NotificationsConfigurationImpl.getInstanceImpl().changeSettings(SamebugNotifications.SAMEBUG_SEARCH_NOTIFICATIONS, NotificationDisplayType.NONE, false, false);
            ApplicationSettings newSettings = new ApplicationSettings(state.get());
            newSettings.wereNotificationsDisabled = true;
            saveSettings(newSettings);
        }

        ApplicationManager.getApplication().executeOnPooledThread(new Runnable() {
            @Override
            public void run() {
                ApplicationSettings newSettings = new ApplicationSettings(state.get());
                if (newSettings.apiKey == null) {
                    SettingsDialog.setup(null);
                } else {
                    // TODO
//                    try {
//                        UserInfo userInfo = profileService.loadUserInfo(newSettings.apiKey);
//                        if (userInfo.getUserExist()) {
//                            newSettings.userId = userInfo.getUserId();
//                            newSettings.avatarUrl = userInfo.getAvatarUrl().toString();
//                            saveSettings(newSettings);
//                        }
//                    } catch (SamebugClientException e) {
//                        LOGGER.warn("Failed to get user info", e);
//                    }
                }
            }
        });

        MessageBus messageBus = ApplicationManager.getApplication().getMessageBus();
        connection = messageBus.connect();
        clientService = new ClientService(messageBus);
        clientService.configure(state.get().getNetworkConfig());
        historyService = new HistoryService(messageBus, clientService);
        profileService = new ProfileService(messageBus, clientService);
        solutionService = new SolutionService(messageBus, clientService);
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
        if (clientService != null) {
            clientService.configure(newSettings.getNetworkConfig());
        }
        urlBuilder = new WebUrlBuilder(newSettings.serverRoot);
    }
}
