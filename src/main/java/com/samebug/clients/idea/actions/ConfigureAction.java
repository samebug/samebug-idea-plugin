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
package com.samebug.clients.idea.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.options.ShowSettingsUtil;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.ProjectManager;
import com.samebug.clients.common.services.ClientService;
import com.samebug.clients.idea.components.application.IdeaSamebugPlugin;
import com.samebug.clients.idea.ui.controller.ConfigDialog;
import com.samebug.clients.swing.ui.global.IconService;
import com.samebug.clients.swing.ui.global.MessageService;

final public class ConfigureAction extends AnAction implements DumbAware {
    @Override
    public void actionPerformed(AnActionEvent e) {
        ShowSettingsUtil.getInstance().showSettingsDialog(ProjectManager.getInstance().getDefaultProject(), ConfigDialog.class);
    }

    @Override
    public void update(AnActionEvent e) {
        ClientService connectionService = IdeaSamebugPlugin.getInstance().clientService;
        if (connectionService.isConnected() && !connectionService.isAuthenticated()) {
            e.getPresentation().setText(MessageService.message("samebug.actions.configure.text.invalidApiKey"));
            e.getPresentation().setIcon(IconService.cogwheelTodo);
        } else {
            e.getPresentation().setText(MessageService.message("samebug.actions.configure.text.ok"));
            e.getPresentation().setIcon(IconService.cogwheel);
        }
    }
}
