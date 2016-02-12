/**
 * Copyright 2016 Samebug, Inc.
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.samebug.clients.idea.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindowManager;
import com.samebug.clients.idea.ui.SamebugToolWindowFactory;

public class HistoryAction extends AnAction {
    // TODO disable action when user is not logged in

    @Override
    public void actionPerformed(AnActionEvent e) {
        Project project = e.getProject();
        SamebugToolWindowFactory toolWindow = (SamebugToolWindowFactory) ToolWindowManager.getInstance(project).getToolWindow("Samebug");
        toolWindow.loadHistory();
    }
}
