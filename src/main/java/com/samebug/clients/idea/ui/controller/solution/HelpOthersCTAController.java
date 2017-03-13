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
package com.samebug.clients.idea.ui.controller.solution;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.util.messages.MessageBusConnection;
import com.samebug.clients.common.api.client.RestError;
import com.samebug.clients.common.api.entities.solution.RestHit;
import com.samebug.clients.common.api.entities.solution.Tip;
import com.samebug.clients.common.api.exceptions.SamebugClientException;
import com.samebug.clients.common.api.form.FieldError;
import com.samebug.clients.common.api.form.FormBuilder;
import com.samebug.clients.common.services.SolutionService;
import com.samebug.clients.common.ui.component.community.IHelpOthersCTA;
import com.samebug.clients.common.ui.component.form.FormMismatchException;
import com.samebug.clients.idea.components.application.IdeaSamebugPlugin;
import com.samebug.clients.idea.ui.controller.form.FormHandler;
import com.samebug.clients.idea.ui.modules.IdeaListenerService;
import com.samebug.clients.swing.ui.modules.MessageService;

import java.util.List;

final class HelpOthersCTAController implements IHelpOthersCTA.Listener {
    final static Logger LOGGER = Logger.getInstance(HelpOthersCTAController.class);
    final SolutionsController controller;

    public HelpOthersCTAController(final SolutionsController controller) {
        this.controller = controller;

        MessageBusConnection projectConnection = controller.myProject.getMessageBus().connect(controller);
        projectConnection.subscribe(IdeaListenerService.HelpOthersCTA, this);
    }

    @Override
    public void postTip(final IHelpOthersCTA source, final String tipBody) {
        LOGGER.debug("post tips clicked");
        source.startPostTip();
        ApplicationManager.getApplication().executeOnPooledThread(new Runnable() {
            @Override
            public void run() {
                final SolutionService solutionService = IdeaSamebugPlugin.getInstance().solutionService;
                new FormHandler() {

                    @Override
                    protected void attempt() throws SamebugClientException {
                        final RestHit<Tip> response = solutionService.postTip(controller.searchId, tipBody, null);
                        ApplicationManager.getApplication().invokeLater(new Runnable() {
                            @Override
                            public void run() {
                                controller.load();
                                source.successPostTip();
                            }
                        });
                    }

                    @Override
                    protected void handle(FieldError fieldError, List<String> globalErrors, List<FieldError> fieldErrors) {
                        if (FormBuilder.CreateTip.BODY.equals(fieldError.key)) fieldErrors.add(fieldError);
                        else {
                            LOGGER.warn("Unhandled form error: " + fieldError);
                            globalErrors.add(MessageService.message("samebug.error.pluginBug"));
                        }
                    }

                    @Override
                    protected void handle(RestError nonFormError, List<String> globalErrors, List<FieldError> fieldErrors) {
                        LOGGER.warn("Unhandled bad request: " + nonFormError);
                        globalErrors.add(MessageService.message("samebug.component.tip.write.error.badRequest"));
                    }

                    @Override
                    protected void handle(SamebugClientException exception, List<String> globalErrors, List<FieldError> fieldErrors) {
                        LOGGER.warn("Failed to post tip", exception);
                        globalErrors.add(MessageService.message("samebug.component.tip.write.error.unhandled"));
                    }

                    @Override
                    protected void showFieldErrors(List<FieldError> fieldErrors) throws FormMismatchException {
                        source.failPostTipWithFormError(fieldErrors);
                    }

                    @Override
                    protected void showGlobalErrors(List<String> globalErrors) {
                        // TODO showing more errors?
                        if (!globalErrors.isEmpty()) controller.view.popupError(globalErrors.get(0));
                    }
                }.execute();
            }
        });
    }
}
