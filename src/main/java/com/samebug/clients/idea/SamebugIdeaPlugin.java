package com.samebug.clients.idea;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.ApplicationComponent;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.extensions.PluginId;
import com.samebug.clients.idea.intellij.autosearch.StackTraceSearch;
import com.samebug.clients.rest.SamebugClient;
import com.samebug.clients.rest.entities.UserInfo;
import com.samebug.clients.rest.exceptions.SamebugClientException;
import com.samebug.clients.exceptions.UnknownApiKey;
import com.samebug.clients.idea.intellij.autosearch.android.AndroidShellSolutionSearch;
import com.samebug.clients.idea.intellij.notification.SamebugNotification;
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
    private StackTraceSearch stackTraceSearch;

    public SamebugIdeaPlugin() {
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

    @NotNull
    public static SamebugClient getClient() {
        return getInstance().client;
    }

    @Nullable
    public static StackTraceSearch getSearchEngine() {
        return getInstance().stackTraceSearch;
    }

    public static boolean isInitialized() {
        return getInstance().getApiKey() != null;
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
    }

    private static final PluginId PLUGIN_ID = PluginId.getId("com.samebug.clients.idea");
    private final SamebugClient client;
    private SamebugState state = new SamebugState();
}
