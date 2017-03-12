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
package com.samebug.clients.idea.ui.controller.tipRequestList;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.util.messages.MessageBus;
import com.samebug.clients.common.ui.frame.tipRequestList.ITipRequestListFrame;
import com.samebug.clients.idea.components.project.ToolWindowController;
import com.samebug.clients.idea.ui.controller.frame.ConnectionStatusController;
import com.samebug.clients.idea.ui.modules.IdeaDataService;
import com.samebug.clients.swing.ui.frame.tipRequestList.TipRequestListFrame;
import com.samebug.clients.swing.ui.modules.DataService;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public final class TipRequestListController implements Disposable {
    final static Logger LOGGER = Logger.getInstance(TipRequestListController.class);
    final ToolWindowController twc;
    final Project myProject;
    final ITipRequestListFrame view;

    final ConnectionStatusController connectionStatusController;


    public TipRequestListController(ToolWindowController twc, Project project) {
        this.twc = twc;
        this.myProject = project;
        view = new TipRequestListFrame();
        DataService.putData((TipRequestListFrame) view, IdeaDataService.Project, project);

        MessageBus messageBus = myProject.getMessageBus();
        connectionStatusController = new ConnectionStatusController(view, messageBus);

    }

    @NotNull
    public JComponent getControlPanel() {
        return (TipRequestListFrame) view;
    }

    @Override
    public void dispose() {
        connectionStatusController.dispose();
    }

}
