/*
 * Copyright 2018 Samebug, Inc.
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

import com.intellij.ide.plugins.IdeaPluginDescriptor;
import com.intellij.ide.plugins.PluginManager;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.application.ApplicationInfo;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.ApplicationComponent;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.extensions.PluginId;
import com.intellij.util.messages.MessageBus;
import com.intellij.util.messages.MessageBusConnection;
import com.intellij.util.net.HttpConfigurable;
import com.samebug.clients.common.services.*;
import com.samebug.clients.common.ui.modules.MessageService;
import com.samebug.clients.common.ui.modules.TrackingService;
import com.samebug.clients.http.client.Config;
import com.samebug.clients.http.client.ProxyConfig;
import com.samebug.clients.http.exceptions.SamebugException;
import com.samebug.clients.idea.controllers.ConsoleSearchController;
import com.samebug.clients.idea.controllers.TimedTasks;
import com.samebug.clients.idea.tracking.IdeaRawEvent;
import com.samebug.clients.idea.tracking.IdeaTrackingService;
import com.samebug.clients.idea.ui.controller.frame.ConcurrencyService;
import com.samebug.clients.idea.ui.controller.frame.ConversionService;
import com.samebug.clients.idea.ui.controller.toolwindow.ConfigChangeListener;
import com.samebug.clients.idea.ui.modules.IdeaColorService;
import com.samebug.clients.idea.ui.modules.IdeaIconService;
import com.samebug.clients.idea.ui.modules.IdeaListenerService;
import com.samebug.clients.idea.ui.modules.IdeaMessageService;
import com.samebug.clients.swing.ui.modules.*;
import com.samebug.util.SBUtil;
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
    public static final String ID = "Samebug";
    private AtomicReference<ApplicationSettings> state = new AtomicReference<ApplicationSettings>(new ApplicationSettings());

    public String applicationUserAgent;
    public WebUriBuilder uriBuilder = new WebUriBuilder(state.get().serverRoot);
    public IdeaClientService clientService;
    public ProfileStore profileStore;
    public ProfileService profileService;
    public ChatService chatService;
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
        ApplicationInfo appInfo = ApplicationInfo.getInstance();
        IdeaPluginDescriptor plugin = PluginManager.getPlugin(PluginId.getId(ID));
        assert plugin != null : "Samebug plugin is not registered!";
        applicationUserAgent = "Samebug-Plugin" + "/" + plugin.getVersion()
                + " IntelliJ/" + appInfo.getApiVersion()
                + " (" + System.getProperty("os.name") + "/" + System.getProperty("os.version") + ")";

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
        clientService.configure(getNetworkConfig(state.get()));
        profileStore = new ProfileStore();
        profileService = new ProfileService(clientService, profileStore);
        chatService = new ChatService(clientService);
        solutionService = new SolutionService(clientService);
        searchStore = new SearchStore();
        searchService = new SearchService(clientService, searchStore);
        searchRequestStore = new SearchRequestStore();
        searchRequestService = new SearchRequestService(searchRequestStore);
        helpRequestStore = new HelpRequestStore();
        helpRequestService = new HelpRequestService(clientService, helpRequestStore);
        authenticationService = new AuthenticationService(clientService);
        conversionService = new ConversionService();
        concurrencyService = new ConcurrencyService(profileStore, profileService,
                solutionService,
                helpRequestStore, helpRequestService,
                searchService);

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

        // If authentication data (apiKey or workspace id) is changed, we have to do some cleanup
        if (!SBUtil.equals(newSettings.apiKey, oldSettings.apiKey) || !SBUtil.equals(newSettings.workspaceId, oldSettings.workspaceId)) {
            // clear the caches
            helpRequestStore.invalidate();
            profileStore.invalidate();
        }

        state.set(newSettings);
        try {
            if (clientService != null) clientService.configure(getNetworkConfig(newSettings));
            uriBuilder = new WebUriBuilder(newSettings.serverRoot);
            ApplicationManager.getApplication().getMessageBus().syncPublisher(ConfigChangeListener.TOPIC).configChange(oldSettings, newSettings);
        } finally {
            if (!SBUtil.equals(newSettings.apiKey, oldSettings.apiKey)) TrackingService.trace(IdeaRawEvent.changeApiKey());
            if (!SBUtil.equals(newSettings.workspaceId, oldSettings.workspaceId)) TrackingService.trace(IdeaRawEvent.changeWorkspace());
        }
    }

    @Override
    public void loadState(ApplicationSettings state) {
        ApplicationSettings newSettings = new ApplicationSettings(state);
        this.state.set(newSettings);
        if (clientService != null) clientService.configure(getNetworkConfig(newSettings));
        uriBuilder = new WebUriBuilder(newSettings.serverRoot);
    }

    @NotNull
    private Config getNetworkConfig(@NotNull ApplicationSettings settings) {
        ProxyConfig proxyConfig;
        try {
            final HttpConfigurable iConfig = HttpConfigurable.getInstance();
            if (iConfig.isHttpProxyEnabledForUrl(settings.serverRoot)) {
                proxyConfig = new ProxyConfig(iConfig.PROXY_HOST, iConfig.PROXY_PORT, iConfig.getProxyLogin(), iConfig.getPlainProxyPassword());
            } else {
                proxyConfig = null;
            }
        } catch (Exception ignored) {
            // if that fails, we pretend there is no proxy. This might fail do to subtle changes in the HttpConfigurable class between intellij versions.
            proxyConfig = null;
        }
        return new Config(settings.apiKey, settings.userId, settings.workspaceId, settings.serverRoot,
                settings.trackingRoot, settings.isTrackingEnabled, settings.connectTimeout, settings.requestTimeout,
                settings.isApacheLoggingEnabled, settings.isJsonDebugEnabled, proxyConfig, applicationUserAgent);
    }
}
