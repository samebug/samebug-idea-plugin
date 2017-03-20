package com.samebug.clients.idea.ui.controller.form;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.samebug.clients.common.api.client.FormRestError;
import com.samebug.clients.common.api.client.RestError;
import com.samebug.clients.common.api.entities.helpRequest.MyHelpRequest;
import com.samebug.clients.common.api.exceptions.BadRequest;
import com.samebug.clients.common.api.exceptions.SamebugClientException;
import com.samebug.clients.common.api.form.CreateHelpRequest;
import com.samebug.clients.common.api.form.FieldError;
import com.samebug.clients.common.services.HelpRequestService;
import com.samebug.clients.common.ui.component.community.IAskForHelp;
import com.samebug.clients.common.ui.component.form.FormMismatchException;
import com.samebug.clients.common.ui.frame.IFrame;
import com.samebug.clients.idea.components.application.IdeaSamebugPlugin;
import com.samebug.clients.swing.ui.modules.MessageService;

import java.util.ArrayList;
import java.util.List;

public abstract class CreateHelpRequestFormHandler {
    final static Logger LOGGER = Logger.getInstance(CreateHelpRequestFormHandler.class);

    final IFrame frame;
    final IAskForHelp form;
    final CreateHelpRequest data;

    public CreateHelpRequestFormHandler(IFrame frame, IAskForHelp form, CreateHelpRequest data) {
        this.frame = frame;
        this.form = form;
        this.data = data;
    }

    public final void execute() {
        beforePostForm();
        ApplicationManager.getApplication().executeOnPooledThread(new Runnable() {
            @Override
            public void run() {
                try {
                    final MyHelpRequest response = postForm();
                    ApplicationManager.getApplication().invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            form.successRequestTip();
                            afterPostForm(response);
                        }
                    });
                } catch (SamebugClientException e) {
                    handleException(e);
                }
            }
        });
    }

    protected void beforePostForm() {
        form.startRequestTip();
    }

    protected MyHelpRequest postForm() throws SamebugClientException {
        final HelpRequestService helpRequestService = IdeaSamebugPlugin.getInstance().helpRequestService;
        return helpRequestService.createHelpRequest(data.searchId, data.context);
    }

    protected void handleFieldError(FieldError fieldError, List<String> globalErrors, List<FieldError> fieldErrors) {
        if (CreateHelpRequest.CONTEXT.equals(fieldError.key)) fieldErrors.add(fieldError);
        else {
            LOGGER.warn("Unhandled form error: " + fieldError);
            globalErrors.add(MessageService.message("samebug.error.pluginBug"));
        }
    }

    protected void handleNonFormBadRequests(RestError nonFormError, List<String> globalErrors, List<FieldError> fieldErrors) {
        // TODO reload?
        if (nonFormError.code.equals(CreateHelpRequest.E_DUPLICATE_HELP_REQUEST)) globalErrors.add(MessageService.message("samebug.component.helpRequest.ask.error.duplicate"));
        else {
            LOGGER.warn("Unhandled bad request: " + nonFormError);
            globalErrors.add(MessageService.message("samebug.component.helpRequest.ask.error.badRequest"));
        }
    }

    protected void handleOtherClientExceptions(SamebugClientException exception, List<String> globalErrors, List<FieldError> fieldErrors) {
        LOGGER.warn("Failed to post help request", exception);
        globalErrors.add(MessageService.message("samebug.component.helpRequest.ask.error.unhandled"));
    }

    protected void showFieldErrors(List<FieldError> fieldErrors) throws FormMismatchException {
        form.failRequestTip(fieldErrors);
    }

    protected void showGlobalErrors(List<String> globalErrors) {
        if (!globalErrors.isEmpty()) frame.popupError(globalErrors.get(0));
    }

    private void handleException(SamebugClientException exception) {
        final List<String> globalErrors = new ArrayList<String>();
        final List<FieldError> fieldErrors = new ArrayList<FieldError>();

        if (exception instanceof BadRequest) {
            if (((BadRequest) exception).getRestError() instanceof FormRestError) {
                FormRestError formError = (FormRestError) ((BadRequest) exception).getRestError();
                for (FieldError fieldError : formError.getAllFieldErrors()) handleFieldError(fieldError, globalErrors, fieldErrors);
            } else {
                RestError otherError = (RestError) ((BadRequest) exception).getRestError();
                handleNonFormBadRequests(otherError, globalErrors, fieldErrors);
            }
        } else {
            handleOtherClientExceptions(exception, globalErrors, fieldErrors);
        }

        ApplicationManager.getApplication().invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    showFieldErrors(fieldErrors);
                } catch (FormMismatchException formException) {
                    LOGGER.warn("Unprocessed form errors", formException);
                    globalErrors.add(MessageService.message("samebug.error.pluginBug"));
                }

                showGlobalErrors(globalErrors);
            }
        });
    }


    protected abstract void afterPostForm(MyHelpRequest response);
}
