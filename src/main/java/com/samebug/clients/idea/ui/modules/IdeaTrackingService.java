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
package com.samebug.clients.idea.ui.modules;

import com.intellij.ide.DataManager;
import com.intellij.ide.plugins.IdeaPluginDescriptor;
import com.intellij.ide.plugins.PluginManager;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.actionSystem.DataKeys;
import com.intellij.openapi.application.ApplicationInfo;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.extensions.PluginId;
import com.intellij.openapi.project.Project;
import com.intellij.util.messages.MessageBusConnection;
import com.intellij.util.ui.UIUtil;
import com.samebug.clients.http.client.SamebugClient;
import com.samebug.clients.http.entities.tracking.TrackEvent;
import com.samebug.clients.idea.components.application.ApplicationSettings;
import com.samebug.clients.idea.components.application.IdeaSamebugPlugin;
import com.samebug.clients.idea.messages.ConfigChangeListener;
import com.samebug.clients.idea.tracking.TrackBuilder;
import com.samebug.clients.swing.ui.modules.TrackingService;

import javax.swing.*;
import java.util.HashMap;
import java.util.Map;

public final class IdeaTrackingService extends TrackingService implements ConfigChangeListener {
    private static final Logger LOGGER = Logger.getInstance(IdeaTrackingService.class);
    private ApplicationSettings config;

    public IdeaTrackingService(MessageBusConnection connection, ApplicationSettings config) {
        connection.subscribe(ConfigChangeListener.TOPIC, this);
        this.config = config;
    }

    @Override
    public void configChange(ApplicationSettings oldSettings, ApplicationSettings newSettings) {
        config = newSettings;
    }

    protected void internalTrace(TrackEvent event) {
        if (config.isTrackingEnabled && event != null) {
            try {
                try {
                    addAppInfo(event);
                } catch (Exception ignored) {
                }
                try {
                    addUserInfo(event);
                } catch (Exception ignored) {
                }
                try {
                    if (!event.fields.containsKey("project")) addProjectInfo(event);
                } catch (Exception ignored) {
                }
                try {
                    addEnvironmentInfo(event);
                } catch (Exception ignored) {
                }
                SamebugClient client = IdeaSamebugPlugin.getInstance().clientService.getClient();
                client.trace(event);
            } catch (Exception e) {
                LOGGER.debug("Failed to report tracking event", e);
            }
        }
    }

    // IMPROVE TrackEvent is not pure
    private void addAppInfo(TrackEvent e) {
        Map<String, String> intellijInfo = new HashMap<String, String>();
        LookAndFeel laf = UIManager.getLookAndFeel();
        ApplicationInfo appInfo = ApplicationInfo.getInstance();
        if (laf != null) intellijInfo.put("lookAndFeel", laf.getName());
        intellijInfo.put("ideaApiVersion", appInfo.getApiVersion());
        intellijInfo.put("ideaFullVersion", appInfo.getFullVersion());
        intellijInfo.put("ideaVersionName", appInfo.getVersionName());
        e.fields.put("intellijInfo", intellijInfo);

        IdeaPluginDescriptor plugin = PluginManager.getPlugin(PluginId.getId("Samebug"));
        String pluginVersion = plugin == null ? null : plugin.getVersion();
        if (pluginVersion != null) e.fields.put("pluginVersion", pluginVersion);
    }

    private void addUserInfo(TrackEvent e) {
        Integer userId = config.userId;
        if (userId != null) e.fields.put("userId", userId);

        Integer workspaceId = config.workspaceId;
        if (workspaceId != null) e.fields.put("workspaceId", workspaceId);

        String instanceId = config.instanceId;
        if (instanceId != null) e.fields.put("instanceId", instanceId);
    }

    private void addProjectInfo(TrackEvent e) {
        // get project from focus
        DataContext dataContext = DataManager.getInstance().getDataContextFromFocus().getResult();
        Project project = DataKeys.PROJECT.getData(dataContext);

        if (project != null) e.fields.put("project", TrackBuilder.projectData(project));
    }

    private void addEnvironmentInfo(TrackEvent e) {
        Map<String, String> environmentInfo = new HashMap<String, String>();
        environmentInfo.put("os_name", System.getProperty("os.name"));
        environmentInfo.put("os_version", System.getProperty("os.version"));
        environmentInfo.put("java_version", System.getProperty("java.version"));
        environmentInfo.put("java_runtime_version", System.getProperty("java.runtime.version"));
        environmentInfo.put("is_retina", Boolean.toString(UIUtil.isRetina()));
        e.fields.put("environmentInfo", environmentInfo);
    }
}
