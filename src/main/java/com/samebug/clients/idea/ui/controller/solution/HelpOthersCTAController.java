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
package com.samebug.clients.idea.ui.controller.solution;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.util.messages.MessageBusConnection;
import com.samebug.clients.common.search.api.entities.RestHit;
import com.samebug.clients.common.search.api.entities.Tip;
import com.samebug.clients.common.search.api.exceptions.BadRequest;
import com.samebug.clients.common.search.api.exceptions.SamebugClientException;
import com.samebug.clients.common.services.SolutionService;
import com.samebug.clients.common.ui.component.solutions.IHelpOthersCTA;
import com.samebug.clients.idea.components.application.IdeaSamebugPlugin;
import com.samebug.clients.idea.ui.global.IdeaListenerService;
import com.samebug.clients.swing.ui.global.MessageService;

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
                            controller.loadLazy();
                            source.successPostTip();
                        }
                    });
                } catch (SamebugClientException e) {
                    final String errorMessage;
                    // TODO refine this error message part
                    if (e instanceof BadRequest) {
                        final String writeTipErrorCode = ((BadRequest) e).getRestError().getCode();
                        if ("UNRECOGNIZED_SOURCE".equals(writeTipErrorCode)) errorMessage = MessageService.message("samebug.component.tip.write.error.source.malformed");
                        else if ("MESSAGE_TOO_SHORT".equals(writeTipErrorCode)) errorMessage = MessageService.message("samebug.component.tip.write.error.tip.short");
                        else if ("MESSAGE_TOO_LONG".equals(writeTipErrorCode)) errorMessage = MessageService.message("samebug.component.tip.write.error.tip.long");
                        else if ("NOT_YOUR_SEARCH".equals(writeTipErrorCode)) errorMessage = MessageService.message("samebug.component.tip.write.error.notYourSearch");
                        else if ("NOT_EXCEPTION_SEARCH".equals(writeTipErrorCode)) errorMessage = MessageService.message("samebug.component.tip.write.error.notExceptionSearch");
                        else if ("UNKNOWN_SEARCH".equals(writeTipErrorCode)) errorMessage = MessageService.message("samebug.component.tip.write.error.unknownSearch");
                        else if ("UNREACHABLE_SOURCE".equals(writeTipErrorCode)) errorMessage = MessageService.message("samebug.component.tip.write.error.unreachableSource");
                        else errorMessage = MessageService.message("samebug.component.tip.write.error.source.unhandledBadRequest");
                    } else {
                        errorMessage = MessageService.message("samebug.component.tip.write.error.source.unhandled");
                        LOGGER.warn("unhandled exception after post tip", e);
                    }
                    ApplicationManager.getApplication().invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            source.interruptPostTip();
                            controller.view.popupError(errorMessage);
                        }
                    });
                }
            }
        });
    }
}
