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


import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.samebug.clients.common.api.entities.search.CreatedSearch;
import com.samebug.clients.common.api.entities.tracking.TrackEvent;
import com.samebug.clients.common.entities.search.DebugSessionInfo;
import com.samebug.clients.common.entities.search.SearchInfo;
import com.samebug.clients.idea.ui.controller.frame.BaseFrameController;

import javax.swing.*;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public final class Events {
    public static TrackEvent pluginInstall() {
        return new TrackBuilder("Plugin", "FirstRun").getEvent();
    }

    public static TrackEvent configOpen() {
        return new TrackBuilder("Settings", "Open").getEvent();
    }

    public static TrackEvent changeApiKey() {
        return new TrackBuilder("Settings", "ChangeApiKey").getEvent();
    }

    public static TrackEvent changeWorkspace() {
        return new TrackBuilder("Settings", "ChangeWorkspace").getEvent();
    }

    public static TrackEvent projectOpen(final Project project) {
        return new TrackBuilder("Project", "Open") {
            protected void initFields() {
                add("projectName", project.getName());
            }
        }.getEvent();
    }

    public static TrackEvent projectClose(final Project project) {
        return new TrackBuilder("Project", "Close") {
            protected void initFields() {
                add("projectName", project.getName());
            }
        }.getEvent();
    }

    public static TrackEvent debugStart(final DebugSessionInfo debugSessionInfo) {
        return new TrackBuilder("Debug", "Start") {
            protected void initFields() {
                add("sessionId", debugSessionInfo.getId().toString());
                add("sessionType", debugSessionInfo.getSessionType());
            }
        }.getEvent();
    }

    public static TrackEvent debugStop(Project project, final DebugSessionInfo debugSessionInfo) {
        return new TrackBuilder("Debug", "Stop") {
            protected void initFields() {
                add("sessionId", debugSessionInfo.getId().toString());
                add("sessionType", debugSessionInfo.getSessionType());
            }
        }.getEvent();
    }

    public static TrackEvent searchSucceeded(final SearchInfo searchInfo, final CreatedSearch createdSearch) {
        return new TrackBuilder("Search", "Succeeded") {
            protected void initFields() {
                add("searchId", createdSearch.getSearchId());
                add("sessionId", searchInfo.getSessionInfo().getId().toString());
                add("sessionType", searchInfo.getSessionInfo().getSessionType());
            }
        }.getEvent();
    }

    public static TrackEvent toolWindowOpen(final Project project, final int width, final int height) {
        return new TrackBuilder("ToolWindow", "Open") {
            protected void initFields() {
                add("projectName", project.getName());
                add("screenWidth", width);
                add("screenHeight", height);
            }
        }.getEvent();
    }

    public static TrackEvent showLoginScreen(final BaseFrameController controller) {
        return new ShowToolwindowBuilder("ToolWindow", "Show", controller) {
            protected void initFields() {
                add("screenName", LOGIN);
            }
        }.getEvent();
    }

    public static TrackEvent showHelpRequestListScreen(final BaseFrameController controller) {
        return new ShowToolwindowBuilder("ToolWindow", "Show", controller) {
            protected void initFields() {
                add("screenName", HELP_REQUEST_LIST);
            }
        }.getEvent();
    }

    public static TrackEvent showHelpRequestScreen(final BaseFrameController controller, final String helpRequestId) {
        return new ShowToolwindowBuilder("ToolWindow", "Show", controller) {
            protected void initFields() {
                add("screenName", HELP_REQUEST);
                add("helpRequestId", helpRequestId);
            }
        }.getEvent();
    }

    public static TrackEvent showSolutionsScreen(final BaseFrameController controller, final int searchId) {
        return new ShowToolwindowBuilder("ToolWindow", "Show", controller) {
            protected void initFields() {
                add("screenName", SOLUTIONS);
                add("searchId", searchId);
            }
        }.getEvent();
    }

    public static TrackEvent linkClick(final URL url) {
        return new TrackBuilder("Link", "Click") {
            protected void initFields() {
                add("url", url.toString());
            }
        }.getEvent();
    }

    public static TrackEvent searchClick(final Project project, final int searchId) {
        return new TrackBuilder("Search", "Click") {
            protected void initFields() {
                add("searchId", searchId);
            }
        }.getEvent();
    }

    public static TrackEvent writeTipOpen(final Project project, final int searchId) {
        return new TrackBuilder("WriteTip", "Open") {
            protected void initFields() {
                add("searchId", searchId);
            }
        }.getEvent();
    }

    public static TrackEvent writeTipCancel(final Project project, final int searchId) {
        return new TrackBuilder("WriteTip", "Cancel") {
            protected void initFields() {
                add("searchId", searchId);
            }
        }.getEvent();
    }

    public static TrackEvent writeTipSubmit(final Project project, final int searchId, final String tip, final String sourceUrl, final String result) {
        return new TrackBuilder("WriteTip", "Submit") {
            protected void initFields() {
                add("searchId", searchId);
                add("tip", tip);
                add("sourceUrl", sourceUrl);
                add("result", result);
            }
        }.getEvent();
    }

    public static TrackEvent markSubmit(final Project project, final int searchId, final int solutionId, final String result) {
        return new TrackBuilder("Mark", "Submit") {
            protected void initFields() {
                add("searchId", searchId);
                add("solutionId", solutionId);
                add("result", result);
            }
        }.getEvent();
    }

    public static TrackEvent openSearchDialog() {
        return new TrackBuilder("SearchDialog", "Open").getEvent();
    }

    public static TrackEvent searchInSearchDialog() {
        return new TrackBuilder("SearchDialog", "Search").getEvent();
    }

    public static TrackEvent searchSucceedInSearchDialog(final int searchId) {
        return new TrackBuilder("SearchDialog", "SearchSucceed") {
            protected void initFields() {
                add("searchId", searchId);
            }
        }.getEvent();
    }

    public static TrackEvent gutterIconClicked(final int searchId) {
        return new TrackBuilder("Gutter", "Clicked") {
            protected void initFields() {
                add("searchId", searchId);
            }
        }.getEvent();
    }

    public static TrackEvent gutterIconTooltip(final int searchId) {
        return new TrackBuilder("Gutter", "Tooltip") {
            protected void initFields() {
                add("searchId", searchId);
            }
        }.getEvent();
    }

    private final static Logger LOGGER = Logger.getInstance(Events.class);
    private static final String LOGIN = "Login";
    private static final String HELP_REQUEST_LIST = "HelpRequestList";
    private static final String HELP_REQUEST = "HelpRequest";
    private static final String SOLUTIONS = "Solutions";

    /**
     * Use TrackBuilder to make sure that the creation of a TrackEvent will not leak exception to the caller.
     */
    private static class TrackBuilder {
        final String category;
        final String action;
        final Map<String, Object> fields = new HashMap<String, Object>();

        public TrackBuilder(String category, String action) {
            this.category = category;
            this.action = action;
        }

        protected void initFields() {
        }

        protected void add(String fieldName, Object value) {
            fields.put(fieldName, value);
        }

        final public TrackEvent getEvent() {
            try {
                initFields();
                return new TrackEvent(fields);
            } catch (Exception e) {
                LOGGER.debug("Failed to send tracking event", e);
                return null;
            }
        }
    }

    private static class ShowToolwindowBuilder extends TrackBuilder {

        public ShowToolwindowBuilder(String category, String action, BaseFrameController controller) {
            super(category, action);
            try {
                JComponent v = (JComponent) controller.view;
                add("screenWidth", v.getWidth());
                add("screenHeight", v.getHeight());
            } catch (Exception ignored) {
            }
        }
    }
}
