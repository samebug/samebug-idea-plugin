/**
 * Copyright 2017 Samebug, Inc.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.samebug.clients.idea.ui.controller.expsearch;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.util.messages.MessageBusConnection;
import com.samebug.clients.common.messages.SolutionModelListener;
import com.samebug.clients.common.search.api.entities.Solutions;
import com.samebug.clients.idea.ui.component.solutions.SolutionFrame;
import org.jetbrains.annotations.NotNull;

final class ModelController implements SolutionModelListener {
    final static Logger LOGGER = Logger.getInstance(ModelController.class);
    @NotNull
    final SolutionFrameController controller;

    public ModelController(@NotNull final SolutionFrameController controller) {
        this.controller = controller;

        MessageBusConnection projectConnection = controller.myProject.getMessageBus().connect(controller);
        projectConnection.subscribe(SolutionModelListener.TOPIC, this);
    }

    @Override
    public void startLoadingSolutions(int searchId) {
        ApplicationManager.getApplication().invokeLater(new Runnable() {
            @Override
            public void run() {
                controller.view.setWarningLoading();
            }
        });
    }

    @Override
    public void successLoadingSolutions(int searchId, Solutions result) {
        final SolutionFrame.Model model = controller.convert(result);

        ApplicationManager.getApplication().invokeLater(new Runnable() {
            @Override
            public void run() {
                controller.view.setContent(model);
            }
        });
    }

    @Override
    public void failLoadingSolutions(int searchId, Exception e) {
        ApplicationManager.getApplication().invokeLater(new Runnable() {
            @Override
            public void run() {
                controller.view.setWarningLoading();
            }
        });
    }

    @Override
    public void finishLoadingSolutions(int searchId) {

    }
}
