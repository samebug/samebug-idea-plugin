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
package com.samebug.clients.idea.ui.controller.solution;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.util.messages.MessageBusConnection;
import com.samebug.clients.common.ui.component.solutions.IExceptionHeaderPanel;
import com.samebug.clients.idea.components.application.IdeaSamebugPlugin;
import com.samebug.clients.idea.ui.BrowserUtil;
import com.samebug.clients.idea.ui.global.IdeaListenerService;

import java.net.URL;

final class ExceptionHeaderController implements IExceptionHeaderPanel.Listener {
    final static Logger LOGGER = Logger.getInstance(ExceptionHeaderController.class);

    final SolutionsController controller;

    public ExceptionHeaderController(final SolutionsController controller) {
        this.controller = controller;

        MessageBusConnection projectConnection = controller.myProject.getMessageBus().connect(controller);
        projectConnection.subscribe(IdeaListenerService.ExceptionHeaderPanel, this);
    }

    @Override
    public void titleClicked() {
        final URL searchUrl = IdeaSamebugPlugin.getInstance().urlBuilder.search(controller.searchId);
        BrowserUtil.browse(searchUrl);
    }
}