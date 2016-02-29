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
package com.samebug.clients.idea.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.DumbAware;
import com.samebug.clients.idea.components.application.IdeaClientService;
import com.samebug.clients.idea.components.application.IdeaSamebugPlugin;
import com.samebug.clients.idea.ui.SamebugHistoryWindow;

public class ReloadHistoryAction extends AnAction implements DumbAware {
    private SamebugHistoryWindow historyWindow;

    @Override
    public void actionPerformed(AnActionEvent e) {
        e.getPresentation().setEnabled(false);
        historyWindow.loadHistory();
    }

    @Override
    public void update(AnActionEvent e) {
        IdeaSamebugPlugin plugin = IdeaSamebugPlugin.getInstance();
        IdeaClientService client = plugin.getClient();
        e.getPresentation().setEnabled(client.getNumberOfActiveRequests() == 0 && plugin.isInitialized());
    }

    public void setHook(SamebugHistoryWindow historyWindow) {
        this.historyWindow = historyWindow;
    }
}