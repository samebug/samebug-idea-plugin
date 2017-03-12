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
import com.samebug.clients.common.api.entities.solution.RestHit;
import com.samebug.clients.common.api.entities.solution.Tip;
import com.samebug.clients.common.api.exceptions.BadRequest;
import com.samebug.clients.common.api.exceptions.SamebugClientException;
import com.samebug.clients.common.api.form.FormError;
import com.samebug.clients.common.services.SolutionService;
import com.samebug.clients.common.ui.component.community.IHelpOthersCTA;
import com.samebug.clients.common.ui.component.form.FormMismatchException;
import com.samebug.clients.idea.components.application.IdeaSamebugPlugin;
import com.samebug.clients.idea.ui.modules.IdeaListenerService;

import java.util.ArrayList;
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
                SolutionService solutionService = IdeaSamebugPlugin.getInstance().solutionService;
                try {
                    final RestHit<Tip> response = solutionService.postTip(controller.searchId, tipBody, null);
                    ApplicationManager.getApplication().invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            controller.load();
                            source.successPostTip();
                        }
                    });
                } catch (SamebugClientException e) {
                    final List<String> globalErrors = new ArrayList<String>();
                    final List<FormError> formErrors = new ArrayList<FormError>();

                    if (e instanceof BadRequest) {
                        // TODO if it is FORM_ERROR
                        if (true) {
                            // TODO add form errors that belong to fields
                            // TODO add the rest to global errors
                        } else {
                            // TODO add these to global errors
                        }
                    } else {
                        // TODO add this to global errors
                    }

                    ApplicationManager.getApplication().invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            if (!formErrors.isEmpty()) {
                                try {
                                    source.failPostTipWithFormError(formErrors);
                                } catch (FormMismatchException formException) {
                                    LOGGER.warn("Unprocessed form errors after posting tip", formException);
                                    globalErrors.add("TODO: failed to add field errors");
                                }
                            } else {
                                source.interruptPostTip();
                            }

                            // TODO show global errors
                            controller.view.popupError(null);
                        }
                    });

//                    // TODO refine this error message part
//                    if (e instanceof BadRequest) {
//                        // TODO extract formerror processing
//                        final String writeTipErrorCode = ((BadRequest) e).getRestError().getCode();
//                        if ("FORM_ERROR".equals(writeTipErrorCode)) {
//                            final List<FormError> formErrors = null; // TODO form errors with nonnull keys
//                            if (!formErrors.isEmpty()) {
//                                ApplicationManager.getApplication().invokeLater(new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        try {
//                                            source.failPostTipWithFormError(formErrors);
//                                        } catch (FormMismatchException formException) {
//                                            LOGGER.warn("Unprocessed form errors after posting tip", formException);
//                                        }
//                                    }
//                                });
//                            }
//                            String globalErrorCode = null; // TODO
//                            if ("NOT_YOUR_SEARCH".equals(globalErrorCode)) globalErrorMessage = MessageService.message("samebug.component.tip.write.error.notYourSearch");
//                            else if ("NOT_EXCEPTION_SEARCH".equals(globalErrorCode)) globalErrorMessage = MessageService.message("samebug.component.tip.write.error.notExceptionSearch");
//                            else if ("UNKNOWN_SEARCH".equals(globalErrorCode)) globalErrorMessage = MessageService.message("samebug.component.tip.write.error.unknownSearch");
//                            else if ("UNREACHABLE_SOURCE".equals(globalErrorCode)) globalErrorMessage = MessageService.message("samebug.component.tip.write.error.unreachableSource");
//                            else LOGGER.warn("Unhandled global form error with code " + globalErrorCode);
//                        } else {
//                            // TODO handle non-form errors
//                        }
//                    } else {
//                        globalErrorMessage = MessageService.message("samebug.component.tip.write.error.source.unhandled");
//                        LOGGER.warn("unhandled exception after post tip", e);
//                    }
                }
            }
        });
    }
}
