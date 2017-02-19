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
import com.samebug.clients.common.search.api.entities.MarkResponse;
import com.samebug.clients.common.search.api.exceptions.SamebugClientException;
import com.samebug.clients.common.services.SolutionService;
import com.samebug.clients.common.ui.component.solutions.IMarkButton;
import com.samebug.clients.idea.components.application.IdeaSamebugPlugin;
import com.samebug.clients.idea.messages.view.RefreshTimestampsListener;
import com.samebug.clients.idea.ui.BrowserUtil;
import com.samebug.clients.idea.ui.component.solutions.ExceptionHeaderPanel;
import com.samebug.clients.idea.ui.component.solutions.MarkButton;
import org.jetbrains.annotations.NotNull;

import java.net.URL;

final class ViewController implements RefreshTimestampsListener {
    final static Logger LOGGER = Logger.getInstance(ViewController.class);
    @NotNull
    final SolutionFrameController controller;

    public ViewController(@NotNull final SolutionFrameController controller) {
        this.controller = controller;

        MessageBusConnection projectConnection = controller.myProject.getMessageBus().connect(controller);
        projectConnection.subscribe(RefreshTimestampsListener.TOPIC, this);
        projectConnection.subscribe(ExceptionHeaderPanel.Listener.TOPIC, new ExceptionHeaderPanelController());
        projectConnection.subscribe(MarkButton.Listener.TOPIC, new MarkPanelController());
    }

    @Override
    public void refreshDateLabels() {
    }

    private final class ExceptionHeaderPanelController implements ExceptionHeaderPanel.Listener {
        @Override
        public void titleClicked() {
            final URL searchUrl = IdeaSamebugPlugin.getInstance().getUrlBuilder().search(controller.searchId);
            BrowserUtil.browse(searchUrl);
        }
    }

    private final class MarkPanelController implements IMarkButton.Listener {
        @Override
        public void markClicked(final IMarkButton markButton, final Integer solutionId, final Integer markId) {
            markButton.setLoading();
            ApplicationManager.getApplication().executeOnPooledThread(new Runnable() {
                @Override
                public void run() {
                    SolutionService solutionService = IdeaSamebugPlugin.getInstance().getSolutionService();
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
                        markButton.setError();
                    }
                }
            });
        }
    }
}
