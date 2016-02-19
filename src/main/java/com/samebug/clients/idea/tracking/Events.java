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
        return new TrackBuilder("plugin-install", null) {
        }.getEvent();
    }

    public static TrackEvent apiKeySet() {
        return new TrackBuilder("apikey-set", null) {
        }.getEvent();
    }

    public static TrackEvent projectOpen(Project project) {
        return new TrackBuilder("project-open", project) {
            @Override
            protected void initFields() {
                String lookAndFeelName = null;
                if (UIManager.getLookAndFeel() != null) lookAndFeelName = UIManager.getLookAndFeel().getName();
                fields.put("lookAndFeel", lookAndFeelName);
            }
        }.getEvent();
    }

    public static TrackEvent projectClose(Project project) {
        return new TrackBuilder("project-close", project) {
        }.getEvent();
    }

    public static TrackEvent debugStart(Project project, final DebugSessionInfo debugSessionInfo) {
        return new TrackBuilder("debug-start", project) {
            @Override
            protected void initFields() {
                fields.put("sessionInfo", debugSessionInfo.getId().toString());
                fields.put("debugSessionType", debugSessionInfo.getSessionType());
            }
        }.getEvent();
    }

    public static TrackEvent debugStop(Project project, final DebugSessionInfo debugSessionInfo) {
        return new TrackBuilder("debug-stop", project) {
            @Override
            protected void initFields() {
                fields.put("sessionInfo", debugSessionInfo.getId().toString());
                fields.put("debugSessionType", debugSessionInfo.getSessionType());
            }
        }.getEvent();
    }

    public static TrackEvent searchSucceeded(final SearchInfo searchInfo, final SearchResults searchResults) {
        return new TrackBuilder("search-succeeded", null) {
            @Override
            protected void initFields() {
                fields.put("searchId", searchResults.searchId);
                fields.put("sessionInfo", searchInfo.getSessionInfo().getId().toString());
                fields.put("debugSessionType", searchInfo.getSessionInfo().getSessionType());
            }
        }.getEvent();
    }

    public static TrackEvent toolWindowOpen(Project project, final String from) {
        return new TrackBuilder("toolWindow-open", project) {
            @Override
            protected void initFields() {
                fields.put("from", from);
            }
        }.getEvent();
    }

    public static TrackEvent linkClick(Project project, final URL url) {
        return new TrackBuilder("link-click", project) {
            @Override
            protected void initFields() {
                String link = null;
                if (url != null) link = url.toString();
                fields.put("link", link);
            }
        }.getEvent();
    }

    private final static Logger LOGGER = Logger.getInstance(Events.class);

    private static abstract class TrackBuilder {
        final protected String eventType;
        final protected Project project;
        final protected Map<String, String> fields = new HashMap<String, String>();

        public TrackBuilder(String eventType, @Nullable Project project) {
            this.eventType = eventType;
            this.project = project;
        }

        protected void initFields() {
        }

        private void addCommonFields() {
            fields.put("type", eventType);
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

        final public TrackEvent getEvent() {

            try {
                initFields();
                addCommonFields();
                return new TrackEvent(fields);
            } catch (Exception e) {
                LOGGER.debug("Failed to send tracking event", e);
                return null;
            }
        }
    }
}
