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
import com.samebug.clients.common.api.entities.helpRequest.MyHelpRequest;
import com.samebug.clients.common.api.exceptions.SamebugClientException;
import com.samebug.clients.common.services.HelpRequestService;
import com.samebug.clients.common.ui.component.helpRequest.IMyHelpRequest;
import com.samebug.clients.idea.components.application.IdeaSamebugPlugin;
import com.samebug.clients.swing.ui.modules.MessageService;

// TODO ALREADY_REVOKED is not handled. Post request handling should be generalized
final class RevokeHelpRequestListener implements IMyHelpRequest.Listener {
    final static Logger LOGGER = Logger.getInstance(RevokeHelpRequestListener.class);

    final SolutionFrameController controller;

    public RevokeHelpRequestListener(final SolutionFrameController controller) {
        this.controller = controller;
    }

    @Override
    public void revokeHelpRequest(final IMyHelpRequest source, final String helpRequestId) {
        source.startRevoke();
        ApplicationManager.getApplication().executeOnPooledThread(new Runnable() {
            @Override
            public void run() {
                final HelpRequestService helpRequestService = IdeaSamebugPlugin.getInstance().helpRequestService;
                try {
                    final MyHelpRequest response = helpRequestService.revokeHelpRequest(helpRequestId);
                    ApplicationManager.getApplication().invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            controller.load();
                            source.successRevoke();
                        }
                    });
                } catch (SamebugClientException e) {
                    LOGGER.warn("Failed to revoke help request", e);
                    ApplicationManager.getApplication().invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            source.failRevoke();
                            controller.view.popupError(MessageService.message("samebug.component.helpRequest.revoke.error.unhandled"));
                        }
                    });
                }
            }
        });
    }
}
