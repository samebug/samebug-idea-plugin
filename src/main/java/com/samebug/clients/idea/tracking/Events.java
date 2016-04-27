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
import com.intellij.openapi.application.ApplicationInfo;
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

final public class Events {
    public static TrackEvent pluginInstall() {
        return new TrackBuilder("Plugin", "FirstRun", null) {
        }.getEvent();
    }

    public static TrackEvent apiKeySet() {
        return new TrackBuilder("Settings", "ChangeApiKey", null) {
        }.getEvent();
    }

    public static TrackEvent projectOpen(Project project) {
        return new TrackBuilder("Project", "Open", project) {
            @Override
            protected void initFields() {
                Map<String, String> intellijInfo = new HashMap<String, String>();
                LookAndFeel laf = UIManager.getLookAndFeel();
                ApplicationInfo appInfo = ApplicationInfo.getInstance();
                if (laf != null) intellijInfo.put("lookAndFeel", laf.getName());
                intellijInfo.put("ideaApiVersion", appInfo.getApiVersion());
                intellijInfo.put("ideaFullVersion", appInfo.getFullVersion());
                intellijInfo.put("ideaVersionName", appInfo.getVersionName());
                fields.put("intellijInfo", intellijInfo);
            }
        }.getEvent();
    }

    public static TrackEvent projectClose(Project project) {
        return new TrackBuilder("Project", "Close", project) {
        }.getEvent();
    }

    public static TrackEvent debugStart(Project project, final DebugSessionInfo debugSessionInfo) {
        return new TrackBuilder("Debug", "Start", project) {
            @Override
            protected void initFields() {
                fields.put("sessionId", debugSessionInfo.getId().toString());
                fields.put("sessionType", debugSessionInfo.getSessionType());
            }
        }.getEvent();
    }

    public static TrackEvent debugStop(Project project, final DebugSessionInfo debugSessionInfo) {
        return new TrackBuilder("Debug", "Stop", project) {
            @Override
            protected void initFields() {
                fields.put("sessionId", debugSessionInfo.getId().toString());
                fields.put("sessionType", debugSessionInfo.getSessionType());
            }
        }.getEvent();
    }

    public static TrackEvent searchSucceeded(final SearchInfo searchInfo, final SearchResults searchResults) {
        return new TrackBuilder("Search", "Succeeded", null) {
            @Override
            protected void initFields() {
                fields.put("searchId", Integer.parseInt(searchResults.searchId));
                fields.put("sessionId", searchInfo.getSessionInfo().getId().toString());
                fields.put("sessionType", searchInfo.getSessionInfo().getSessionType());
            }
        }.getEvent();
    }

    public static TrackEvent toolWindowOpen(Project project, final String from) {
        return new TrackBuilder("ToolWindow", "Open", project) {
            @Override
            protected void initFields() {
                fields.put("location", from);
            }
        }.getEvent();
    }

    public static TrackEvent linkClick(Project project, final URL url) {
        return new TrackBuilder("Link", "Click", project) {
            @Override
            protected void initFields() {
                String link = null;
                if (url != null) link = url.toString();
                fields.put("url", link);
            }
        }.getEvent();
    }

    public static TrackEvent searchClick(final Project project, final int searchId) {
        return new TrackBuilder("Search", "Click", project) {
            @Override
            protected void initFields() {
                fields.put("searchId", searchId);
            }
        }.getEvent();
    }

    public static TrackEvent configOpen() {
        return new TrackBuilder("Configuration", "Open", null) {
        }.getEvent();
    }

    public static TrackEvent writeTipOpen(final Project project, final int searchId) {
        return new TrackBuilder("WriteTip", "Open", project) {
            @Override
            protected void initFields() {
                fields.put("searchId", searchId);
            }
        }.getEvent();
    }

    public static TrackEvent writeTipCancel(final Project project, final int searchId) {
        return new TrackBuilder("WriteTip", "Cancel", project) {
            @Override
            protected void initFields() {
                fields.put("searchId", searchId);
            }
        }.getEvent();
    }

    public static TrackEvent writeTipSubmit(final Project project, final int searchId, final String tip, final String sourceUrl, final String result) {
        return new TrackBuilder("WriteTip", "Submit", project) {
            @Override
            protected void initFields() {
                fields.put("searchId", searchId);
                fields.put("tip", tip);
                fields.put("sourceUrl", sourceUrl);
                fields.put("result", result);
            }
        }.getEvent();
    }

    public static TrackEvent markSubmit(final Project project, final int searchId, final int solutionId, final String result) {
        return new TrackBuilder("Mark", "Submit", project) {
            @Override
            protected void initFields() {
                fields.put("searchId", searchId);
                fields.put("solutionId", solutionId);
                fields.put("result", result);
            }
        }.getEvent();
    }

    private final static Logger LOGGER = Logger.getInstance(Events.class);

    private static abstract class TrackBuilder {
        final protected String category;
        final protected String action;
        final protected Project project;
        final protected Map<String, Object> fields = new HashMap<String, Object>();

        public TrackBuilder(String category, String action, @Nullable Project project) {
            this.category = category;
            this.action = action;
            this.project = project;
        }

        protected void initFields() {
        }

        private void addCommonFields() {
            fields.put("category", category);
            fields.put("action", action);
            try {
                Integer userId = IdeaSamebugPlugin.getInstance().getState().userId;
                if (userId != null) fields.put("userId", userId);
            } catch (Exception e) {
                LOGGER.debug("failed to write userId to tracking event", e);
            }
            try {
                String instanceId = IdeaSamebugPlugin.getInstance().getState().instanceId;
                if (instanceId != null) fields.put("instanceId", instanceId);
            } catch (Exception e) {
                LOGGER.debug("failed to write instanceId to tracking event", e);
            }
            try {
                String pluginVersion = PluginManager.getPlugin(PluginId.getId("Samebug")).getVersion();
                if (pluginVersion != null) fields.put("pluginVersion", pluginVersion);
            } catch (Exception e) {
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
