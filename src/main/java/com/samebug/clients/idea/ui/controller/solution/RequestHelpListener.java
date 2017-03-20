package com.samebug.clients.idea.ui.controller.solution;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.samebug.clients.common.api.client.RestError;
import com.samebug.clients.common.api.entities.helpRequest.MyHelpRequest;
import com.samebug.clients.common.api.exceptions.SamebugClientException;
import com.samebug.clients.common.api.form.CreateHelpRequest;
import com.samebug.clients.common.api.form.FieldError;
import com.samebug.clients.common.services.HelpRequestService;
import com.samebug.clients.common.ui.component.community.IAskForHelp;
import com.samebug.clients.common.ui.component.form.FormMismatchException;
import com.samebug.clients.idea.components.application.IdeaSamebugPlugin;
import com.samebug.clients.idea.ui.controller.form.FormHandler;
import com.samebug.clients.swing.ui.modules.MessageService;

import java.util.List;

final class RequestHelpListener implements IAskForHelp.Listener {
    final static Logger LOGGER = Logger.getInstance(RequestHelpListener.class);

    final SolutionFrameController controller;

    public RequestHelpListener(final SolutionFrameController controller) {
        this.controller = controller;
    }

    @Override
    public void askBugmates(final IAskForHelp source, final String description) {
        source.startRequestTip();
        ApplicationManager.getApplication().executeOnPooledThread(new Runnable() {
            @Override
            public void run() {
                final HelpRequestService helpRequestService = IdeaSamebugPlugin.getInstance().helpRequestService;
                new FormHandler() {

                    @Override
                    protected void attempt() throws SamebugClientException {
                        final MyHelpRequest response = helpRequestService.createHelpRequest(controller.searchId, description);
                        ApplicationManager.getApplication().invokeLater(new Runnable() {
                            @Override
                            public void run() {
                                controller.load();
                                source.successRequestTip();
                            }
                        });
                    }

                    @Override
                    protected void handle(FieldError fieldError, List<String> globalErrors, List<FieldError> fieldErrors) {
                        if (CreateHelpRequest.CONTEXT.equals(fieldError.key)) fieldErrors.add(fieldError);
                        else {
                            LOGGER.warn("Unhandled form error: " + fieldError);
                            globalErrors.add(MessageService.message("samebug.error.pluginBug"));
                        }
                    }

                    @Override
                    protected void handle(RestError nonFormError, List<String> globalErrors, List<FieldError> fieldErrors) {
                        LOGGER.warn("Unhandled bad request: " + nonFormError);
                        globalErrors.add(MessageService.message("samebug.component.helpRequest.ask.error.badRequest"));
                    }

                    @Override
                    protected void handle(SamebugClientException exception, List<String> globalErrors, List<FieldError> fieldErrors) {
                        LOGGER.warn("Failed to post help request", exception);
                        globalErrors.add(MessageService.message("samebug.component.helpRequest.ask.error.unhandled"));
                    }

                    @Override
                    protected void showFieldErrors(List<FieldError> fieldErrors) throws FormMismatchException {
                        source.failRequestTip(fieldErrors);
                    }

                    @Override
                    protected void showGlobalErrors(List<String> globalErrors) {
                        if (!globalErrors.isEmpty()) controller.view.popupError(globalErrors.get(0));
                    }
                }.execute();
            }
        });
    }

}
