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

import com.intellij.ide.actions.RefreshAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.DumbAware;
import com.samebug.clients.idea.components.application.IdeaClientService;
import com.samebug.clients.idea.components.application.IdeaSamebugPlugin;
import com.samebug.clients.idea.messages.HistoryListener;

public class ReloadHistoryAction extends RefreshAction implements DumbAware {
    @Override
    public void actionPerformed(AnActionEvent e) {
        e.getPresentation().setEnabled(false);
        e.getProject().getMessageBus().syncPublisher(HistoryListener.UPDATE_HISTORY_TOPIC).reload();
    }

    @Override
    public void update(AnActionEvent e) {
        IdeaSamebugPlugin plugin = IdeaSamebugPlugin.getInstance();
        IdeaClientService client = plugin.getClient();
        e.getPresentation().setEnabled(client.getNumberOfActiveRequests() == 0 && plugin.isInitialized());
    }
}
