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
package com.samebug.clients.idea.ui.controller.authentication;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.util.messages.MessageBus;
import com.samebug.clients.common.ui.frame.IAuthenticationFrame;
import com.samebug.clients.idea.ui.controller.toolwindow.ToolWindowController;
import com.samebug.clients.idea.ui.controller.frame.ConnectionStatusController;
import com.samebug.clients.idea.ui.modules.IdeaDataService;
import com.samebug.clients.swing.ui.frame.authentication.AuthenticationFrame;
import com.samebug.clients.swing.ui.modules.DataService;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public final class AuthenticationController implements Disposable {
    final static Logger LOGGER = Logger.getInstance(AuthenticationController.class);
    final ToolWindowController twc;
    final Project myProject;
    final IAuthenticationFrame view;
    final ConnectionStatusController connectionStatusController;

    public AuthenticationController(ToolWindowController twc, Project project) {
        this.twc = twc;
        this.myProject = project;
        view = new AuthenticationFrame();
        DataService.putData((AuthenticationFrame) view, IdeaDataService.Project, project);

        MessageBus messageBus = myProject.getMessageBus();
        connectionStatusController = new ConnectionStatusController(view, messageBus);
    }

    @NotNull
    public JComponent getControlPanel() {
        return (AuthenticationFrame) view;
    }

    @Override
    public void dispose() {
        connectionStatusController.dispose();
    }
}
