package com.samebug.clients.idea.ui.controller.form;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.samebug.clients.common.api.client.FormRestError;
import com.samebug.clients.common.api.client.RestError;
import com.samebug.clients.common.api.exceptions.BadRequest;
import com.samebug.clients.common.api.exceptions.SamebugClientException;
import com.samebug.clients.common.api.form.FieldError;
import com.samebug.clients.common.ui.component.form.FormMismatchException;
import com.samebug.clients.swing.ui.modules.MessageService;

import java.util.ArrayList;
import java.util.List;

public abstract class FormHandler {
    final static Logger LOGGER = Logger.getInstance(FormHandler.class);

    public void execute() {
        try {
            attempt();
        } catch (SamebugClientException e) {
            handleException(e);
        }
    }

    protected abstract void attempt() throws SamebugClientException;
    protected abstract void handle(FieldError fieldError, List<String> globalErrors, List<FieldError> fieldErrors);
    protected abstract void handle(RestError nonFormError, List<String> globalErrors, List<FieldError> fieldErrors);
    protected abstract void handle(SamebugClientException exception, List<String> globalErrors, List<FieldError> fieldErrors);
    protected abstract void showFieldErrors(List<FieldError> fieldErrors) throws FormMismatchException;
    protected abstract void showGlobalErrors(List<String> globalErrors);

    private void handleException(SamebugClientException exception) {
        final List<String> globalErrors = new ArrayList<String>();
        final List<FieldError> fieldErrors = new ArrayList<FieldError>();

        if (exception instanceof BadRequest) {
            if (((BadRequest) exception).getRestError() instanceof FormRestError) {
                FormRestError formError = (FormRestError) ((BadRequest) exception).getRestError();
                for (FieldError fieldError : formError.getAllFieldErrors()) handle(fieldError, globalErrors, fieldErrors);
            } else {
                RestError otherError = (RestError) ((BadRequest) exception).getRestError();
                handle(otherError, globalErrors, fieldErrors);
            }
        } else {
            handle(exception, globalErrors, fieldErrors);
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

}
