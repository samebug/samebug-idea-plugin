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
package com.samebug.clients.idea.ui.controller.form;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.samebug.clients.http.response.FormRestError;
import com.samebug.clients.http.response.RestError;
import com.samebug.clients.http.exceptions.BadRequest;
import com.samebug.clients.http.exceptions.SamebugClientException;
import com.samebug.clients.http.form.FieldError;
import com.samebug.clients.common.ui.component.form.FormMismatchException;
import com.samebug.clients.swing.ui.modules.MessageService;

import java.util.ArrayList;
import java.util.List;

public abstract class PostFormHandler<T> {
    private final static Logger LOGGER = Logger.getInstance(PostFormHandler.class);

    public final void execute() {
        beforePostForm();
        ApplicationManager.getApplication().executeOnPooledThread(new Runnable() {
            @Override
            public void run() {
                try {
                    final T response = postForm();
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


    /**
     * Runs on the same thread from where execute() was called
     * This is a place where you can update the form UI for a started request.
     */
    protected abstract void beforePostForm();

    /**
     * Runs on background thread
     */
    protected abstract T postForm() throws SamebugClientException;

    /**
     * Runs on UI thread
     * This is a place where you can update the form UI for a successful request.
     */
    protected abstract void afterPostForm(T response);

    /**
     * Runs on background thread
     */
    protected void handleFieldError(FieldError fieldError, List<String> globalErrors, List<FieldError> fieldErrors) {
        LOGGER.info("Form error: " + fieldError);
    }

    /**
     * Runs on background thread
     */
    protected void handleNonFormBadRequests(RestError nonFormError, List<String> globalErrors, List<FieldError> fieldErrors) {
        LOGGER.info("Bad request: " + nonFormError);
    }

    /**
     * Runs on background thread
     */
    protected void handleOtherClientExceptions(SamebugClientException exception, List<String> globalErrors, List<FieldError> fieldErrors) {
        LOGGER.warn("Failed to post request", exception);
    }

    /**
     * Runs on UI thread.
     * Guaranteed to be called if there was any error (if there were no field errors, the list will be empty).
     * This is a place where you can update the form UI for a failed request.
     */
    protected abstract void showFieldErrors(List<FieldError> fieldErrors) throws FormMismatchException;

    protected abstract void showGlobalErrors(List<String> globalErrors);

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

}
