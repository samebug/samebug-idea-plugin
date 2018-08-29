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
package com.samebug.clients.idea.tracking;

import com.intellij.openapi.project.Project;
import com.samebug.clients.common.entities.search.DebugSessionInfo;
import com.samebug.clients.common.tracking.RawEvent;
import com.samebug.clients.idea.ui.controller.frame.BaseFrameController;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class IdeaRawEvent extends RawEvent {

    public static RawEvent toolWindowOpen(@NotNull final Project project) {
        return new IdeaRawEvent("Interaction", "ToolWindowOpened") {
            protected void lazyFields() {
                withProject(project);
            }
        };
    }

    public static RawEvent toolWindowShowContent(@NotNull final Project project, @NotNull final BaseFrameController controller) {
        return new IdeaRawEvent("Interaction", "ToolWindowContentShowed") {
            protected void lazyFields() {
                withProject(project);
                withToolWindow(controller);
            }
        };
    }

    public static RawEvent projectOpen(@NotNull final Project project) {
        return new IdeaRawEvent("Interaction", "ProjectOpened") {
            protected void lazyFields() {
                withProject(project);
            }
        };
    }

    public static RawEvent projectClose(@NotNull final Project project) {
        return new IdeaRawEvent("Interaction", "ProjectClosed") {
            protected void lazyFields() {
                withProject(project);
            }
        };
    }

    public static RawEvent debugStart(@NotNull final Project project, @NotNull final DebugSessionInfo debugSessionInfo) {
        return new IdeaRawEvent("Interaction", "DebugStarted") {
            protected void lazyFields() {
                withProject(project);
                withDebugSession(debugSessionInfo);
            }
        };
    }

    public static RawEvent debugStop(@NotNull final Project project, @NotNull final DebugSessionInfo debugSessionInfo) {
        return new IdeaRawEvent("Interaction", "DebugStopped") {
            protected void lazyFields() {
                withProject(project);
                withDebugSession(debugSessionInfo);
            }
        };
    }

    public static RawEvent pluginInstall() {
        return new IdeaRawEvent("Interaction", "PluginFirstRun") {
            protected void lazyFields() {
            }
        };
    }

    public static RawEvent configOpen() {
        return new IdeaRawEvent("Interaction", "SettingsOpened") {
            protected void lazyFields() {
            }
        };
    }

    public static RawEvent changeApiKey() {
        return new IdeaRawEvent("Interaction", "SettingsApiKeyChanged") {
            protected void lazyFields() {
            }
        };
    }

    public static RawEvent changeWorkspace() {
        return new IdeaRawEvent("Interaction", "SettingsWorkspaceChanged") {
            protected void lazyFields() {
            }
        };
    }

    public static RawEvent gutterIconClick(@NotNull final Integer searchId, @NotNull final String solutionType) {
        return new IdeaRawEvent("Interaction", "GutterClicked") {
            protected void lazyFields() {
                withData("searchId", searchId);
                withData("solutionType", solutionType);
            }
        };
    }


    protected IdeaRawEvent(@NotNull String category, @NotNull String action) {
        super(category, action);
    }

    @NotNull
    IdeaRawEvent withDebugSession(@NotNull DebugSessionInfo debugSessionInfo) {
        withData("sessionId", debugSessionInfo.getId().toString());
        withData("sessionType", debugSessionInfo.getSessionType());
        return this;
    }

    @NotNull
    IdeaRawEvent withProject(@NotNull Project project) {
        withField("projectName", project.getName());
        return this;
    }

    @NotNull
    IdeaRawEvent withToolWindow(@NotNull BaseFrameController controller) {
        JComponent frame = (JComponent) controller.view;
        withData("screenWidth", frame.getWidth());
        withData("screenHeight", frame.getHeight());
        return this;
    }
}
