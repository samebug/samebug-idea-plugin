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


import com.intellij.openapi.project.Project;
import com.samebug.clients.common.entities.search.DebugSessionInfo;
import com.samebug.clients.http.entities.tracking.TrackEvent;
import com.samebug.clients.idea.ui.controller.frame.BaseFrameController;

import java.net.URI;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class Events {
    public static TrackEvent pluginInstall() {
        return event("Plugin", "FirstRun");
    }

    public static TrackEvent configOpen() {
        return event("Settings", "Open");
    }

    public static TrackEvent changeApiKey() {
        return event("Settings", "ChangeApiKey");
    }

    public static TrackEvent changeWorkspace() {
        return event("Settings", "ChangeWorkspace");
    }

    public static TrackEvent recoveryOpenIdeaSettings() {
        return event("Recovery", "OpenIntelliJSettings");
    }

    public static TrackEvent recoveryOpenSamebugSettings() {
        return event("Recovery", "OpenSamebugSettings");
    }

    public static TrackEvent recoveryReload() {
        return event("Recovery", "Reload");
    }

    public static TrackEvent projectOpen(final Project project) {
        return new TrackBuilder("Project", "Open", project).getEvent();
    }

    public static TrackEvent projectClose(final Project project) {
        return new TrackBuilder("Project", "Close", project).getEvent();
    }

    public static TrackEvent debugStart(Project project, final DebugSessionInfo debugSessionInfo) {
        return new TrackBuilder("Debug", "Start", project) {
            protected void initDataFields() {
                add("sessionId", debugSessionInfo.getId().toString());
                add("sessionType", debugSessionInfo.getSessionType());
            }
        }.getEvent();
    }

    public static TrackEvent debugStop(Project project, final DebugSessionInfo debugSessionInfo) {
        return new TrackBuilder("Debug", "Stop", project) {
            protected void initDataFields() {
                add("sessionId", debugSessionInfo.getId().toString());
                add("sessionType", debugSessionInfo.getSessionType());
            }
        }.getEvent();
    }

    public static TrackEvent toolWindowInitialized(final Project project) {
        return new TrackBuilder("ToolWindow", "Open", project).getEvent();
    }

    public static TrackEvent showLoginScreen(final BaseFrameController controller) {
        return new ShowToolwindowBuilder("Page", "View", controller) {
            protected void initDataFields() {
                add("page-type", "authentication");
            }
        }.getEvent();
    }

    public static TrackEvent showHelpRequestListScreen(final BaseFrameController controller) {
        return new ShowToolwindowBuilder("Page", "View", controller) {
            protected void initDataFields() {
                add("page-type", "user-profile/incoming-help-requests");
            }
        }.getEvent();
    }

    public static TrackEvent showHelpRequestScreen(final BaseFrameController controller, final String helpRequestId) {
        return new ShowToolwindowBuilder("Page", "View", controller) {
            protected void initDataFields() {
                add("page-type", "help-request/");
                add("helpRequestId", helpRequestId);
            }
        }.getEvent();
    }

    public static TrackEvent showSolutionsScreen(final BaseFrameController controller, final Integer searchId) {
        return new ShowToolwindowBuilder("Page", "View", controller) {
            protected void initDataFields() {
                add("page-type", "search/stacktrace");
                add("searchId", searchId);
            }
        }.getEvent();
    }

    public static TrackEvent linkClick(final URI uri) {
        return new TrackBuilder("Link", "Click") {
            protected void initDataFields() {
                add("url", uri.toString());
            }
        }.getEvent();
    }

    public static TrackEvent linkClick(final URL url) {
        return new TrackBuilder("Link", "Click") {
            protected void initDataFields() {
                add("url", url.toString());
            }
        }.getEvent();
    }

    public static TrackEvent more() {
        return event("MoreButton", "Click");
    }

    public static TrackEvent openIncomingRequests() {
        return event("Profile", "OpenIncomingRequests");
    }

    public static TrackEvent registrationDialogSwitched(String dialogType) {
        return event("Registration", "DialogSwitched", mapOf("dialogType", dialogType));
    }

    public static TrackEvent registrationForgottenPasswordClicked() {
        return event("Registration", "ForgottenPasswordClicked", mapOf("login", "credentials"));
    }

    public static TrackEvent registrationSend(String login, String dialogType) {
        return event("Registration", "Send", mapOf("login", login, "dialogType", dialogType));
    }

    public static TrackEvent registrationError(String dialogType, List<String> errors) {
        return event("Registration", "FormError", mapOf("dialogType", dialogType, "errors", errors));
    }

    public static TrackEvent registrationLogInSucceeded() {
        return event("Registration", "LogInSucceeded", mapOf("login", "credentials"));
    }

    public static TrackEvent registrationSignUpSucceeded(String login) {
        return event("Registration", "UserCreated", mapOf("login", login));
    }

    public static TrackEvent writeTipOpen() {
        return event("WriteTip", "Open");
    }

    public static TrackEvent writeTipSend() {
        return event("WriteTip", "Send");
    }

    public static TrackEvent writeTipCancel() {
        return event("WriteTip", "Cancel");
    }

    public static TrackEvent writeTipError(List<String> errors) {
        return event("WriteTip", "FormError", mapOf("errors", errors));
    }

    public static TrackEvent mark() {
        return event("Mark", "Marked");
    }

    public static TrackEvent markCancelled() {
        return event("Mark", "Cancelled");
    }

    public static TrackEvent helpRequestNotificationShow(String helpRequestId) {
        return event("HelpRequestNotification", "Show", mapOf("helpRequestId", helpRequestId));
    }

    public static TrackEvent helpRequestNotificationAnswer() {
        return event("HelpRequestNotification", "Answer");
    }

    public static TrackEvent helpRequestNotificationLater() {
        return event("HelpRequestNotification", "Later");
    }

    public static TrackEvent helpRequestOpen(String helpRequestId) {
        return event("HelpRequestList", "OpenHelpRequest", mapOf("helpRequestId", helpRequestId));
    }

    public static TrackEvent helpRequestDialogSwitched(String dialogType) {
        return event("HelpRequest", "DialogSwitched", mapOf("dialogType", dialogType));
    }

    public static TrackEvent helpRequestOpen() {
        return event("WriteHelpRequest", "Open");
    }

    public static TrackEvent helpRequestSend() {
        return event("WriteHelpRequest", "Send");
    }

    public static TrackEvent helpRequestCancel() {
        return event("WriteHelpRequest", "Cancel");
    }

    public static TrackEvent helpRequestError(List<String> errors) {
        return event("WriteHelpRequest", "FormError", mapOf("errors", errors));
    }

    public static TrackEvent openSearchDialog() {
        return event("SearchDialog", "Open");
    }

    public static TrackEvent searchInSearchDialog() {
        return event("SearchDialog", "Search");
    }

    public static TrackEvent searchSucceedInSearchDialog(final Integer searchId) {
        return event("SearchDialog", "SearchSucceed", mapOf("searchId", searchId));
    }

    public static TrackEvent gutterIconClicked(final Integer searchId) {
        return event("Gutter", "Clicked", mapOf("searchId", searchId));
    }

    public static TrackEvent gutterIconHover(final Integer searchId) {
        return event("Gutter", "Hover", mapOf("searchId", searchId));
    }

    public static TrackEvent solutionDialogSwitched(final String dialogType) {
        return event("Solution", "DialogSwitched", mapOf("dialogType", dialogType));
    }

    public static TrackEvent solutionDisplay(final Integer solutionId, final Integer index) {
        return event("Solution", "Displayed", mapOf("id", solutionId, "index", index));
    }

    public static TrackEvent solutionClick(final Integer searchId, final Integer solutionId, final Integer index) {
        return event("Solution", "Clicked", mapOf("sourceSearchId", searchId, "targetId", solutionId, "index", index));
    }


    private static TrackEvent event(String category, String action) {
        return event(category, action, null);
    }

    private static TrackEvent event(String category, String action, final Map<String, ?> dataMap) {
        return new TrackBuilder(category, action) {
            protected void initDataFields() {
                if (dataMap != null) for (Map.Entry<String, ?> d : dataMap.entrySet()) add(d.getKey(), d.getValue());
            }
        }.getEvent();
    }

    private static Map<String, Object> mapOf(String k1, Object v1) {
        Map<String, Object> result = new HashMap<String, Object>();
        if (v1 != null) result.put(k1, v1);
        return result;
    }

    private static Map<String, Object> mapOf(String k1, Object v1, String k2, Object v2) {
        Map<String, Object> result = new HashMap<String, Object>();
        if (v1 != null) result.put(k1, v1);
        if (v2 != null) result.put(k2, v2);
        return result;
    }

    private static Map<String, Object> mapOf(String k1, Object v1, String k2, Object v2, String k3, Object v3) {
        Map<String, Object> result = new HashMap<String, Object>();
        if (v1 != null) result.put(k1, v1);
        if (v2 != null) result.put(k2, v2);
        if (v3 != null) result.put(k3, v3);
        return result;
    }

    private Events() {}
}
