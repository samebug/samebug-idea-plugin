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
package com.samebug.clients.idea.tracking;


import com.intellij.ide.plugins.PluginManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.extensions.PluginId;
import com.intellij.openapi.project.Project;
import com.samebug.clients.idea.components.application.IdeaSamebugPlugin;
import com.samebug.clients.search.api.entities.SearchResults;
import com.samebug.clients.search.api.entities.tracking.DebugSessionInfo;
import com.samebug.clients.search.api.entities.tracking.SearchInfo;
import com.samebug.clients.search.api.entities.tracking.TrackEvent;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by poroszd on 2/18/16.
 */
public class Events {
    public static TrackEvent pluginInstall() {
        Map<String, String> fields = new HashMap<String, String>();
        fields.put("type", "plugin-install");
        return createEvent(fields, null);
    }

    public static TrackEvent apiKeySet() {
        Map<String, String> fields = new HashMap<String, String>();
        fields.put("type", "apikey-set");
        return createEvent(fields, null);
    }

    public static TrackEvent projectOpen(Project project) {
        Map<String, String> fields = new HashMap<String, String>();
        fields.put("type", "project-open");
        String lookAndFeelName = null;
        if (UIManager.getLookAndFeel() != null) lookAndFeelName = UIManager.getLookAndFeel().getName();
        fields.put("lookAndFeel", lookAndFeelName);
        return createEvent(fields, project);
    }

    public static TrackEvent projectClose(Project project) {
        Map<String, String> fields = new HashMap<String, String>();
        fields.put("type", "project-close");
        return createEvent(fields, project);
    }

    public static TrackEvent debugStart(Project project, DebugSessionInfo debugSessionInfo) {
        Map<String, String> fields = new HashMap<String, String>();
        fields.put("type", "debug-start");
        fields.put("sessionInfo", debugSessionInfo.getId().toString());
        fields.put("debugSessionType", debugSessionInfo.getSessionType());
        return createEvent(fields, project);
    }

    public static TrackEvent debugStop(Project project, DebugSessionInfo debugSessionInfo) {
        Map<String, String> fields = new HashMap<String, String>();
        fields.put("type", "debug-stop");
        fields.put("sessionInfo", debugSessionInfo.getId().toString());
        fields.put("debugSessionType", debugSessionInfo.getSessionType());
        return createEvent(fields, project);
    }

    public static TrackEvent searchSucceeded(SearchInfo searchInfo, SearchResults searchResults) {
        Map<String, String> fields = new HashMap<String, String>();
        fields.put("type", "search-succeeded");
        fields.put("searchId", searchResults.searchId);
        fields.put("sessionInfo", searchInfo.getSessionInfo().getId().toString());
        fields.put("debugSessionType", searchInfo.getSessionInfo().getSessionType());
        return createEvent(fields, null);
    }

    public static TrackEvent toolWindowOpen(Project project, String from) {
        Map<String, String> fields = new HashMap<String, String>();
        fields.put("type", "toolWindow-open");
        fields.put("from", from);
        return createEvent(fields, project);
    }

    public static TrackEvent linkClick(Project project, URL url) {
        Map<String, String> fields = new HashMap<String, String>();
        fields.put("type", "link-click");
        String link = null;
        if (url != null) link = url.toString();
        fields.put("link", link);
        return createEvent(fields, project);
    }


    private static TrackEvent createEvent(Map<String, String> fields, @Nullable Project project) {
        addCommonFields(fields, project);
        return new TrackEvent(fields);
    }

    private static void addCommonFields(Map<String, String> fields, @Nullable Project project) {
        try {
            Integer userId = IdeaSamebugPlugin.getInstance().getState().getUserId();
            if (userId != null) fields.put("userId", userId.toString());
        } catch (Throwable e) {
            LOGGER.debug("failed to write userId to tracking event", e);
        }
        try {
            String instanceId = IdeaSamebugPlugin.getInstance().getState().getInstanceId();
            if (instanceId != null) fields.put("instanceId", instanceId);
        } catch (Throwable e) {
            LOGGER.debug("failed to write instanceId to tracking event", e);
        }
        try {
            String pluginVersion = PluginManager.getPlugin(PluginId.getId("Samebug")).getVersion();
            if (pluginVersion != null) fields.put("pluginVersion", pluginVersion);
        } catch (Throwable e) {
            LOGGER.debug("failed to write pluginVersion to tracking event", e);
        }
        if (project != null) {
            fields.put("projectName", project.getName());
        }

    }

    private final static Logger LOGGER = Logger.getInstance(Events.class);

}
