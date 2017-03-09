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

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.util.messages.MessageBusConnection;
import com.samebug.clients.common.search.api.entities.MarkResponse;
import com.samebug.clients.common.search.api.exceptions.SamebugClientException;
import com.samebug.clients.common.services.SolutionService;
import com.samebug.clients.common.ui.component.hit.IMarkButton;
import com.samebug.clients.idea.components.application.IdeaSamebugPlugin;
import com.samebug.clients.idea.ui.modules.IdeaListenerService;

final class MarkController implements IMarkButton.Listener {
    final static Logger LOGGER = Logger.getInstance(MarkController.class);
    final SolutionsController controller;

    public MarkController(final SolutionsController controller) {
        this.controller = controller;

        MessageBusConnection projectConnection = controller.myProject.getMessageBus().connect(controller);
        projectConnection.subscribe(IdeaListenerService.MarkButton, this);
    }

    @Override
    public void markClicked(final IMarkButton markButton, final Integer solutionId, final Integer markId) {
        markButton.setLoading();
        ApplicationManager.getApplication().executeOnPooledThread(new Runnable() {
            @Override
            public void run() {
                SolutionService solutionService = IdeaSamebugPlugin.getInstance().solutionService;
                try {
                    final IMarkButton.Model newModel;
                    if (markId == null) {
                        final MarkResponse response = solutionService.postMark(controller.searchId, solutionId);
                        newModel = controller.convertMarkResponse(response);
                    } else {
                        final MarkResponse response = solutionService.retractMark(markId);
                        newModel = controller.convertRetractedMarkResponse(response);
                    }
                    ApplicationManager.getApplication().invokeLater(new Runnable() {
                        @Override
                        public void run() {

                            markButton.update(newModel);
                        }
                    });
                } catch (SamebugClientException e) {
                    // TODO refine this error message part
                    ApplicationManager.getApplication().invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            markButton.interruptLoading();
                            controller.view.popupError("Mark failed");
                        }
                    });
                }
            }
        });
    }
}