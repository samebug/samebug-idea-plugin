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

import com.intellij.ide.actions.RefreshAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.samebug.clients.idea.components.application.IdeaClientService;
import com.samebug.clients.idea.components.application.IdeaSamebugPlugin;
import com.samebug.clients.idea.ui.controller.SearchTabControllers;

final public class ReloadSearchAction extends RefreshAction implements DumbAware {
    @Override
    public void actionPerformed(final AnActionEvent e) {
        e.getPresentation().setEnabled(false);
        final Project project = e.getProject();
        if (project != null) {
            ServiceManager.getService(project, SearchTabControllers.class).reloadFocusedSearch();
        }
    }

    @Override
    public void update(AnActionEvent e) {
        IdeaClientService client = IdeaSamebugPlugin.getInstance().getClient();
        e.getPresentation().setEnabled(client.getNumberOfActiveRequests() == 0);
    }

    final static Logger LOGGER = Logger.getInstance(ReloadSearchAction.class);
}
