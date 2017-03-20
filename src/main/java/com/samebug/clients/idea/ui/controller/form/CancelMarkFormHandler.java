package com.samebug.clients.idea.ui.controller.form;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.samebug.clients.common.api.client.FormRestError;
import com.samebug.clients.common.api.client.RestError;
import com.samebug.clients.common.api.entities.solution.MarkResponse;
import com.samebug.clients.common.api.exceptions.BadRequest;
import com.samebug.clients.common.api.exceptions.SamebugClientException;
import com.samebug.clients.common.api.form.CancelMark;
import com.samebug.clients.common.api.form.CreateMark;
import com.samebug.clients.common.api.form.FieldError;
import com.samebug.clients.common.services.SolutionService;
import com.samebug.clients.common.ui.component.form.FormMismatchException;
import com.samebug.clients.common.ui.component.hit.IMarkButton;
import com.samebug.clients.common.ui.frame.IFrame;
import com.samebug.clients.idea.components.application.IdeaSamebugPlugin;
import com.samebug.clients.swing.ui.modules.MessageService;

import java.util.ArrayList;
import java.util.List;

public abstract class CancelMarkFormHandler {
    final static Logger LOGGER = Logger.getInstance(CancelMarkFormHandler.class);

    final IFrame frame;
    final IMarkButton button;
    final CancelMark data;

    public CancelMarkFormHandler(IFrame frame, IMarkButton button, CancelMark data) {
        this.frame = frame;
        this.button = button;
        this.data = data;
    }

    public final void execute() {
        beforePostForm();
        ApplicationManager.getApplication().executeOnPooledThread(new Runnable() {
            @Override
            public void run() {
                try {
                    final MarkResponse response = postForm();
                    ApplicationManager.getApplication().invokeLater(new Runnable() {
                        @Override
                        public void run() {
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
        button.setLoading();
    }

    protected MarkResponse postForm() throws SamebugClientException {
        final SolutionService solutionService = IdeaSamebugPlugin.getInstance().solutionService;
        return solutionService.retractMark(data.markId);
    }

    protected void handleFieldError(FieldError fieldError, List<String> globalErrors, List<FieldError> fieldErrors) {
        LOGGER.warn("Unhandled form error: " + fieldError);
        globalErrors.add(MessageService.message("samebug.error.pluginBug"));
    }

    protected void handleNonFormBadRequests(RestError nonFormError, List<String> globalErrors, List<FieldError> fieldErrors) {
        if (nonFormError.code.equals(CreateMark.E_ALREADY_MARKED)) globalErrors.add(MessageService.message("samebug.component.mark.cancel.error.alreadyCancelled"));
        else {
            LOGGER.warn("Unhandled bad request: " + nonFormError);
            globalErrors.add(MessageService.message("samebug.component.mark.cancel.error.badRequest"));
        }
    }

    protected void handleOtherClientExceptions(SamebugClientException exception, List<String> globalErrors, List<FieldError> fieldErrors) {
        LOGGER.warn("Failed to cancel mark", exception);
        globalErrors.add(MessageService.message("samebug.component.mark.cancel.error.unhandled"));
    }

    protected void showFieldErrors(List<FieldError> fieldErrors) throws FormMismatchException {
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


    protected abstract void afterPostForm(MarkResponse response);
}
