/**
 * Copyright 2017 Samebug, Inc.
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
package com.samebug.clients.idea.ui.controller.expsearch;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.samebug.clients.idea.components.project.ToolWindowController;
import com.samebug.clients.idea.ui.component.experimental.SolutionFrame;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

final public class SolutionFrameController implements Disposable {
    final static Logger LOGGER = Logger.getInstance(SolutionFrameController.class);
    @NotNull
    final ToolWindowController twc;
    @NotNull
    final Project project;

    @NotNull
    final SolutionFrame view;

    public SolutionFrameController(@NotNull ToolWindowController twc, @NotNull Project project, final int searchId) {
        this.twc = twc;
        this.project = project;
        this.view = new SolutionFrame();
    }

    @NotNull
    public JPanel getControlPanel() {
        return view;
    }

    public void reload() {
    }


    void refreshTab() {
    }

    @Override
    public void dispose() {

    }
}

