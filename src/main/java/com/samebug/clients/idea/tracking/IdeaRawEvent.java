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
import com.samebug.clients.common.tracking.RawEvent;
import com.samebug.clients.idea.ui.controller.frame.BaseFrameController;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class IdeaRawEvent extends RawEvent {

    public static RawEvent toolWindowOpen(@NotNull final Project project) {
        return new IdeaRawEvent("Interaction", "ToolWindow-Open") {
            protected void myLazyFields() {
                withProject(project);
            }
        };
    }

    public static RawEvent toolWindowShowContent(@NotNull final Project project, @NotNull final BaseFrameController controller) {
        return new IdeaRawEvent("Interaction", "ToolWindow-ShowContent") {
            protected void myLazyFields() {
                withProject(project);
                withToolWindow(controller);
            }
        };
    }

    public static RawEvent projectOpen(@NotNull final Project project) {
        return new IdeaRawEvent("Interaction", "Project-Open") {
            protected void myLazyFields() {
                withProject(project);
            }
        };
    }

    public static RawEvent projectClose(@NotNull final Project project) {
        return new IdeaRawEvent("Interaction", "Project-Close") {
            protected void myLazyFields() {
                withProject(project);
            }
        };
    }

    public static RawEvent debugStart(@NotNull final Project project, @NotNull final DebugSessionInfo debugSessionInfo) {
        return new IdeaRawEvent("Interaction", "Debug-Start") {
            protected void myLazyFields() {
                withProject(project);
                withDebugSession(debugSessionInfo);
            }
        };
    }

    public static RawEvent debugStop(@NotNull final Project project, @NotNull final DebugSessionInfo debugSessionInfo) {
        return new IdeaRawEvent("Interaction", "Debug-Stop") {
            protected void myLazyFields() {
                withProject(project);
                withDebugSession(debugSessionInfo);
            }
        };
    }

    public static RawEvent pluginInstall() {
        return new IdeaRawEvent("Interaction", "Plugin-FirstRun") {
            protected void myLazyFields() {
            }
        };
    }

    public static RawEvent configOpen() {
        return new IdeaRawEvent("Interaction", "Settings-Open") {
            protected void myLazyFields() {
            }
        };
    }

    public static RawEvent changeApiKey() {
        return new IdeaRawEvent("Interaction", "Settings-ChangeApiKey") {
            protected void myLazyFields() {
            }
        };
    }

    public static RawEvent changeWorkspace() {
        return new IdeaRawEvent("Interaction", "Settings-ChangeWorkspace") {
            protected void myLazyFields() {
            }
        };
    }

    public static RawEvent gutterIconClick(@NotNull final Integer searchId) {
        return new IdeaRawEvent("Interaction", "Gutter-Clicked") {
            protected void myLazyFields() {
                withData("searchId", searchId);
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
