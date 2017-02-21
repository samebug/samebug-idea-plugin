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

import com.intellij.openapi.Disposable;
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
import com.samebug.clients.common.search.api.entities.UserInfo;
import com.samebug.clients.common.search.api.exceptions.SamebugClientException;
import com.samebug.clients.common.services.*;
import com.samebug.clients.idea.controllers.ConsoleSearchController;
import com.samebug.clients.idea.controllers.SessionsController;
import com.samebug.clients.idea.controllers.TimedTasks;
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
final public class IdeaSamebugPlugin implements ApplicationComponent, PersistentStateComponent<ApplicationSettings>, Disposable {
    final private static Logger LOGGER = Logger.getInstance(IdeaSamebugPlugin.class);
    private AtomicReference<ApplicationSettings> state = new AtomicReference<ApplicationSettings>(new ApplicationSettings());

    private WebUrlBuilder urlBuilder = new WebUrlBuilder(state.get().serverRoot);

    @Nullable
    private ClientService clientService;
    @Nullable
    private HistoryService historyService;

    @Nullable
    private ProfileStore profileStore;
    @Nullable
    private ProfileService profileService;

    @Nullable
    private SolutionStore solutionStore;
    @Nullable
    private SolutionService solutionService;

    @Nullable
    private SearchRequestStore searchRequestStore;
    @Nullable
    private SearchRequestService searchRequestService;

    @Nullable
    private SearchStore searchStore;
    @Nullable
    private SearchService searchService;

    @Nullable
    private BugmateStore bugmateStore;
    @Nullable
    private BugmateService bugmateService;
    
    @Nullable
    private AuthenticationListenerImpl authenticationListener;

    @Nullable
    private MessageBusConnection connection;

    @NotNull
    public static IdeaSamebugPlugin getInstance() {
        IdeaSamebugPlugin instance = ApplicationManager.getApplication().getComponent(IdeaSamebugPlugin.class);
        assert instance != null : "Plugin is not initialized!";
        return instance;
    }

    @NotNull
    public WebUrlBuilder getUrlBuilder() {
        return urlBuilder;
    }

    @NotNull
    public ClientService getClient() {
        assert clientService != null : "Plugin is not initialized!";
        return clientService;
    }

    @NotNull
    public ProfileStore getProfileStore() {
        assert profileStore != null : "Plugin is not initialized!";
        return profileStore;
    }

    @NotNull
    public ProfileService getProfileService() {
        assert profileService != null : "Plugin is not initialized!";
        return profileService;
    }

    @NotNull
    public SearchStore getSearchStore() {
        assert searchStore != null : "Plugin is not initialized!";
        return searchStore;
    }

    @NotNull
    public SearchService getSearchService() {
        assert searchService != null : "Plugin is not initialized!";
        return searchService;
    }

    @NotNull
    public SearchRequestStore getSearchRequestStore() {
        assert searchRequestStore != null : "Plugin is not initialized!";
        return searchRequestStore;
    }

    @NotNull
    public SearchRequestService getSearchRequestService() {
        assert searchRequestService != null : "Plugin is not initialized!";
        return searchRequestService;
    }

    @NotNull
    public SolutionStore getSolutionStore() {
        assert solutionStore != null : "Plugin is not initialized!";
        return solutionStore;
    }

    @NotNull
    public SolutionService getSolutionService() {
        assert solutionService != null : "Plugin is not initialized!";
        return solutionService;
    }

    @NotNull
    public BugmateService getBugmateService() {
        assert bugmateService != null : "Plugin is not initialized!";
        return bugmateService;
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
                ApplicationSettings settings = state.get();
                String apiKey = settings.apiKey;
                if (apiKey != null) {
                    try {
                        UserInfo profile = profileService.loadUserInfo(apiKey);
                        if (profile.getUserExist()) {
                            profileService.loadUserStats();
                        }
                    } catch (SamebugClientException ignored) {
                    }
                }
            }
        });
    }

    @Override
    public void initComponent() {
        try {
            FontRegistry.registerFonts();
        } catch (IOException e) {
           LOGGER.error("Failed to read custom fonts file", e);
        } catch (FontFormatException e) {
            LOGGER.error("Failed to read custom fonts file", e);
        }

        MessageBus messageBus = ApplicationManager.getApplication().getMessageBus();
        connection = messageBus.connect(this);
        clientService = new ClientService(messageBus);
        clientService.configure(state.get().getNetworkConfig());

        historyService = new HistoryService(messageBus, clientService);
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

        TimedTasks timedTasks = new TimedTasks(messageBus.connect(this));
        ConsoleSearchController consoleSearchController = new ConsoleSearchController(messageBus.connect(this));
        SessionsController sessionsController = new SessionsController(messageBus.connect(this), searchRequestService, searchRequestStore);

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
