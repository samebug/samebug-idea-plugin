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
package com.samebug.clients.idea.components.application;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.ApplicationComponent;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.util.messages.MessageBus;
import com.intellij.util.messages.MessageBusConnection;
import com.samebug.clients.common.services.*;
import com.samebug.clients.http.client.SamebugClient;
import com.samebug.clients.http.exceptions.SamebugException;
import com.samebug.clients.idea.controllers.ConsoleSearchController;
import com.samebug.clients.idea.controllers.TimedTasks;
import com.samebug.clients.idea.tracking.Events;
import com.samebug.clients.idea.ui.controller.frame.ConcurrencyService;
import com.samebug.clients.idea.ui.controller.frame.ConversionService;
import com.samebug.clients.idea.ui.controller.toolwindow.ConfigChangeListener;
import com.samebug.clients.idea.ui.modules.*;
import com.samebug.clients.swing.ui.modules.*;
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
public final class IdeaSamebugPlugin implements ApplicationComponent, PersistentStateComponent<ApplicationSettings>, Disposable {
    private static final Logger LOGGER = Logger.getInstance(IdeaSamebugPlugin.class);
    private AtomicReference<ApplicationSettings> state = new AtomicReference<ApplicationSettings>(new ApplicationSettings());

    public WebUriBuilder uriBuilder = new WebUriBuilder(state.get().serverRoot);
    public IdeaClientService clientService;
    public ProfileStore profileStore;
    public ProfileService profileService;
    public SolutionService solutionService;
    public SearchRequestStore searchRequestStore;
    public SearchRequestService searchRequestService;
    public SearchStore searchStore;
    public SearchService searchService;
    public HelpRequestStore helpRequestStore;
    public HelpRequestService helpRequestService;
    public AuthenticationService authenticationService;
    public ConversionService conversionService;
    public ConcurrencyService concurrencyService;

    @Nullable
    private MessageBusConnection connection;

    @NotNull
    public static IdeaSamebugPlugin getInstance() {
        IdeaSamebugPlugin instance = ApplicationManager.getApplication().getComponent(IdeaSamebugPlugin.class);
        assert instance != null : "Plugin is not initialized!";
        return instance;
    }

    public void checkAuthenticationInTheBackgroundWithCurrentConfig() {
        assert authenticationService != null;
        ApplicationManager.getApplication().executeOnPooledThread(new Runnable() {
            @Override
            public void run() {
                ApplicationSettings settings = state.get();
                if (settings.apiKey != null) {
                    try {
                        authenticationService.apiKeyAuthentication();
                    } catch (SamebugException ignored) {
                    }
                }
            }
        });
    }

    @Override
    public void initComponent() {
        try {
            FontService.registerFonts();
        } catch (IOException e) {
            LOGGER.error("Failed to read custom fonts file", e);
        } catch (FontFormatException e) {
            LOGGER.error("Failed to read custom fonts file", e);
        }

        MessageBus messageBus = ApplicationManager.getApplication().getMessageBus();
        connection = messageBus.connect(this);
        clientService = new IdeaClientService(messageBus);
        clientService.configure(state.get().getNetworkConfig());
        SamebugClient client = clientService.getClient();
        profileStore = new ProfileStore();
        profileService = new ProfileService(client, profileStore);
        solutionService = new SolutionService(client);
        searchStore = new SearchStore();
        searchService = new SearchService(client, searchStore);
        searchRequestStore = new SearchRequestStore();
        searchRequestService = new SearchRequestService(searchRequestStore);
        helpRequestStore = new HelpRequestStore();
        helpRequestService = new HelpRequestService(client, helpRequestStore);
        authenticationService = new AuthenticationService(client);
        conversionService = new ConversionService();
        concurrencyService = new ConcurrencyService(profileStore, profileService,
                solutionService,
                helpRequestStore, helpRequestService,
                searchStore, searchService);

        TimedTasks timedTasks = new TimedTasks();
        ConsoleSearchController consoleSearchController = new ConsoleSearchController(messageBus.connect(this));

        ColorService.install(new IdeaColorService());
        WebImageService.install();
        IconService.install(new IdeaIconService());
        ListenerService.install(new IdeaListenerService());
        MessageService.install(new IdeaMessageService());
        TrackingService.install(new IdeaTrackingService(connection, state.get()));

        checkAuthenticationInTheBackgroundWithCurrentConfig();
    }

    @Override
    public void disposeComponent() {
        if (connection != null) {
            connection.disconnect();
        }
        if (clientService != null) {
            clientService.dispose();
        }
    }

    @Override
    public void dispose() {
        disposeComponent();
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
        ApplicationSettings oldSettings = state.get();
        ApplicationSettings newSettings = new ApplicationSettings(settings);
        state.set(newSettings);
        try {
            if (clientService != null) clientService.configure(newSettings.getNetworkConfig());
            uriBuilder = new WebUriBuilder(newSettings.serverRoot);
            ApplicationManager.getApplication().getMessageBus().syncPublisher(ConfigChangeListener.TOPIC).configChange(oldSettings, newSettings);
        } finally {
            if (oldSettings.apiKey != newSettings.apiKey) TrackingService.trace(Events.changeApiKey());
            if (oldSettings.workspaceId != newSettings.workspaceId) TrackingService.trace(Events.changeWorkspace());
        }
    }

    @Override
    public void loadState(ApplicationSettings state) {
        ApplicationSettings newSettings = new ApplicationSettings(state);
        this.state.set(newSettings);
        if (clientService != null) clientService.configure(newSettings.getNetworkConfig());
        uriBuilder = new WebUriBuilder(newSettings.serverRoot);
    }
}
