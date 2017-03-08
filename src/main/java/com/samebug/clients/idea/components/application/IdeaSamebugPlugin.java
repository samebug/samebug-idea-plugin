/**
 * Copyright 2017 Samebug, Inc.
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

import com.intellij.openapi.Disposable;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.ApplicationComponent;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.util.messages.MessageBus;
import com.intellij.util.messages.MessageBusConnection;
import com.samebug.clients.common.search.api.WebUrlBuilder;
import com.samebug.clients.common.search.api.exceptions.SamebugClientException;
import com.samebug.clients.common.services.*;
import com.samebug.clients.idea.controllers.ConsoleSearchController;
import com.samebug.clients.idea.controllers.TimedTasks;
import com.samebug.clients.idea.ui.global.*;
import com.samebug.clients.swing.ui.global.*;
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
final public class IdeaSamebugPlugin implements ApplicationComponent, PersistentStateComponent<ApplicationSettings>, Disposable {
    final private static Logger LOGGER = Logger.getInstance(IdeaSamebugPlugin.class);
    private AtomicReference<ApplicationSettings> state = new AtomicReference<ApplicationSettings>(new ApplicationSettings());

    public WebUrlBuilder urlBuilder = new WebUrlBuilder(state.get().serverRoot);
    public ClientService clientService;
    public ProfileStore profileStore;
    public ProfileService profileService;
    public SolutionStore solutionStore;
    public SolutionService solutionService;
    public SearchRequestStore searchRequestStore;
    public SearchRequestService searchRequestService;
    public SearchStore searchStore;
    public SearchService searchService;
    public BugmateStore bugmateStore;
    public BugmateService bugmateService;
    public AuthenticationService authenticationService;

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
                        authenticationService.apiKeyAuthentication(settings.apiKey, settings.workspaceId);
                    } catch (SamebugClientException ignored) {
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
        clientService = new ClientService(messageBus);
        clientService.configure(state.get().getNetworkConfig());
        profileStore = new ProfileStore();
        profileService = new ProfileService(messageBus, clientService, profileStore);
        solutionStore = new SolutionStore();
        solutionService = new SolutionService(messageBus, clientService, solutionStore);
        searchStore = new SearchStore();
        searchService = new SearchService(messageBus, clientService, searchStore);
        searchRequestStore = new SearchRequestStore();
        searchRequestService = new SearchRequestService(searchRequestStore);
        bugmateStore = new BugmateStore();
        bugmateService = new BugmateService(messageBus, clientService, bugmateStore);
        authenticationService = new AuthenticationService(messageBus, clientService);

        TimedTasks timedTasks = new TimedTasks(messageBus.connect(this));
        ConsoleSearchController consoleSearchController = new ConsoleSearchController(messageBus.connect(this));

        ColorService.install(new IdeaColorService());
        WebImageService.install(new IdeaWebImageService(urlBuilder));
        IconService.install(new IdeaIconService());
        ListenerService.install(new IdeaListenerService());
        MessageService.install(new IdeaMessageService());

        checkAuthenticationInTheBackgroundWithCurrentConfig();
    }

    @Override
    public void disposeComponent() {
        if (connection != null) {
            connection.disconnect();
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
}
