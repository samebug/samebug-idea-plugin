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
package com.samebug.clients.idea.tracking;

import com.intellij.ide.DataManager;
import com.intellij.ide.plugins.IdeaPluginDescriptor;
import com.intellij.ide.plugins.PluginManager;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.actionSystem.DataKeys;
import com.intellij.openapi.application.ApplicationInfo;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.extensions.PluginId;
import com.intellij.openapi.project.Project;
import com.intellij.util.messages.MessageBusConnection;
import com.intellij.util.ui.UIUtil;
import com.samebug.clients.common.tracking.TrackedUser;
import com.samebug.clients.common.ui.modules.TrackingService;
import com.samebug.clients.http.client.SamebugClient;
import com.samebug.clients.http.entities.tracking.TrackEvent;
import com.samebug.clients.http.exceptions.SamebugClientException;
import com.samebug.clients.idea.components.application.ApplicationSettings;
import com.samebug.clients.idea.components.application.IdeaSamebugPlugin;
import com.samebug.clients.idea.messages.ConfigChangeListener;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.xml.bind.DatatypeConverter;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public final class IdeaTrackingService extends TrackingService implements ConfigChangeListener {
    private static final Logger LOGGER = Logger.getInstance(IdeaTrackingService.class);
    private ApplicationSettings config;
    private AtomicLong lastEventTimestamp = new AtomicLong(System.currentTimeMillis());
    private String currentSessionId = UUID.randomUUID().toString();

    public IdeaTrackingService(MessageBusConnection connection, ApplicationSettings config) {
        connection.subscribe(ConfigChangeListener.TOPIC, this);
        this.config = config;
    }

    @Override
    public void configChange(ApplicationSettings oldSettings, ApplicationSettings newSettings) {
        config = newSettings;
    }

    protected void internalTrace(com.samebug.clients.common.tracking.RawEvent rawEvent) {
        if (config.isTrackingEnabled && rawEvent != null) {
            try {
                addAgent(rawEvent);
            } catch (Exception ignored) {
            }
            try {
                addUser(rawEvent);
            } catch (Exception ignored) {
            }
            try {
                addProject(rawEvent);
            } catch (Exception ignored) {
            }
            try {
                addContext(rawEvent);
            } catch (Exception ignored) {
            }
            postEventInBackground(rawEvent.getEvent());
        }
    }

    // IMPROVE: use a queue if it eats up the worker threads
    private void postEventInBackground(@NotNull final TrackEvent event) {
        ApplicationManager.getApplication().executeOnPooledThread(new Runnable() {
            @Override
            public void run() {
                SamebugClient client = IdeaSamebugPlugin.getInstance().clientService.getClient();
                try {
                    client.trace(event);
                } catch (SamebugClientException e) {
                    LOGGER.debug("Failed to report tracking event", e);
                }
            }
        });
    }

    private void addAgent(com.samebug.clients.common.tracking.RawEvent e) {
        final Map<String, String> agent = new HashMap<String, String>();
        final LookAndFeel laf = UIManager.getLookAndFeel();
        final ApplicationInfo appInfo = ApplicationInfo.getInstance();
        final IdeaPluginDescriptor plugin = PluginManager.getPlugin(PluginId.getId(IdeaSamebugPlugin.ID));
        final String pluginVersion = plugin == null ? null : plugin.getVersion();
        final String instanceId = config.instanceId;

        agent.put("type", "ide-plugin");
        agent.put("ideCodeName", appInfo.getBuild().getProductCode());
        if (laf != null) agent.put("lookAndFeel", laf.getName());
        if (pluginVersion != null) agent.put("pluginVersion", pluginVersion);
        if (instanceId != null) agent.put("instanceId", instanceId);
        agent.put("isRetina", Boolean.toString(UIUtil.isRetina()));
        agent.put("ideBuild", appInfo.getApiVersion());
        e.withField("agent", agent);
    }

    private void addUser(com.samebug.clients.common.tracking.RawEvent e) {
        TrackedUser user = new TrackedUser(config.userId, config.workspaceId, null);
        e.withField("user", user);
    }

    private void addProject(com.samebug.clients.common.tracking.RawEvent e) {
        // get project from focus
        DataContext dataContext = DataManager.getInstance().getDataContextFromFocus().getResult();
        Project project = DataKeys.PROJECT.getData(dataContext);

        if (project != null) {
            e.withField("projectName", project.getName());
        }
    }

    protected void addContext(com.samebug.clients.common.tracking.RawEvent e) {
        String timestamp = DatatypeConverter.printDateTime(java.util.Calendar.getInstance());
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastEventTimestamp.getAndSet(currentTime) > SESSION_TIMEOUT_MS) {
            currentSessionId = UUID.randomUUID().toString();
        }
        e.withField("localTime", timestamp);
        e.withField("eventId", UUID.randomUUID().toString());
        e.withField("sessionId", currentSessionId);
    }
}
