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
import com.samebug.clients.common.messages.AuthenticationListener;
import com.samebug.clients.common.search.api.WebUrlBuilder;
import com.samebug.clients.common.search.api.exceptions.SamebugClientException;
import com.samebug.clients.common.services.ClientService;
import com.samebug.clients.common.services.HistoryService;
import com.samebug.clients.common.services.ProfileService;
import com.samebug.clients.common.services.SolutionService;
import com.samebug.clients.idea.notification.SamebugNotifications;
import com.samebug.clients.idea.ui.FontRegistry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.io.IOException;
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
    private AuthenticationListenerImpl authenticationListener;

    @Nullable
    private MessageBusConnection connection;

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

    public void authenticate() {
        assert profileService != null;
        ApplicationManager.getApplication().executeOnPooledThread(new Runnable() {
            @Override
            public void run() {
                ApplicationSettings newSettings = new ApplicationSettings(state.get());
                String apiKey = newSettings.apiKey;
                if (apiKey != null) {
                    try {
                        profileService.authenticate(apiKey);
                    } catch (SamebugClientException ignored) {
                    }
                }
            }
        });
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
        try {
            FontRegistry.registerFonts();
        } catch (IOException e) {
            // TODO
            e.printStackTrace();
        } catch (FontFormatException e) {
            e.printStackTrace();
        }

        MessageBus messageBus = ApplicationManager.getApplication().getMessageBus();
        connection = messageBus.connect();
        clientService = new ClientService(messageBus);
        clientService.configure(state.get().getNetworkConfig());
        historyService = new HistoryService(messageBus, clientService);
        profileService = new ProfileService(messageBus, clientService);
        solutionService = new SolutionService(messageBus, clientService);
        timedTasks = new TimedTasks(connection);
        authenticationListener = new AuthenticationListenerImpl();

        connection.subscribe(AuthenticationListener.TOPIC, authenticationListener);

        authenticate();
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

    @Override
    @NotNull
    public ApplicationSettings getState() {
        return new ApplicationSettings(state.get());
    }

    public void saveSettings(final ApplicationSettings settings) {
        ApplicationSettings newSettings = new ApplicationSettings(settings);
        state.set(newSettings);
        if (clientService != null) {
            try {
                clientService.configure(newSettings.getNetworkConfig());
                urlBuilder = new WebUrlBuilder(newSettings.serverRoot);
            } finally {
                // TODO change the event
//            Tracking.appTracking().trace(Events.apiKeySet());
            }
        }
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

    final class AuthenticationListenerImpl implements AuthenticationListener {

        @Override
        public void success(String apiKey) {
            // TODO save workspaceId to application settings when necessary
            ApplicationSettings newSettings = new ApplicationSettings(state.get());
            newSettings.apiKey = apiKey;
            saveSettings(newSettings);
        }

        @Override
        public void fail() {
            // TODO we should notify the user that he has to change the apikey, or else plugin will not work.
            LOGGER.warn("Failed to authenticate");
        }
    }
}
