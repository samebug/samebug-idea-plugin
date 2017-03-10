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
package com.samebug.clients.idea.components.project;

import com.intellij.openapi.components.AbstractProjectComponent;
import com.intellij.openapi.project.Project;

public class SamebugProjectComponent extends AbstractProjectComponent {
    private ToolWindowController toolWindowController;
    private RunDebugWatcher runDebugWatcher;
    private DeprecationNotifier deprecationNotifier;

    public SamebugProjectComponent(Project project) {
        super(project);
    }

    public ToolWindowController getToolWindowController() {
        assert toolWindowController != null : "Project is not opened";
        return toolWindowController;
    }

    @Override
    public void projectOpened() {
        this.toolWindowController = new ToolWindowController(myProject);
        this.runDebugWatcher = new RunDebugWatcher(myProject);
        this.deprecationNotifier = new DeprecationNotifier(myProject);
    }

    @Override
    public void projectClosed() {
        toolWindowController.dispose();
    }

}
