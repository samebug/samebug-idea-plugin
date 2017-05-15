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
import com.samebug.clients.http.exceptions.FormException;
import com.samebug.clients.http.exceptions.SamebugClientException;
import org.jetbrains.annotations.NotNull;

public abstract class PostFormHandler<T, E extends FormException> {
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
                } catch (final SamebugClientException e) {
                    ApplicationManager.getApplication().invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            handleOtherClientExceptions(e);
                        }
                    });
                } catch (final FormException e) {
                    ApplicationManager.getApplication().invokeLater(new Runnable() {
                        @Override
                        @SuppressWarnings("unchecked")
                        public void run() {
                            handleBadRequest((E) e);
                        }
                    });
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
    @NotNull
    protected abstract T postForm() throws E, SamebugClientException;

    /**
     * Runs on UI thread
     * This is a place where you can update the form UI for a successful request.
     */
    protected abstract void afterPostForm(@NotNull T response);

    /**
     * Runs on UI thread.
     * Guaranteed to be called if there was any error (if there were no field errors, the list will be empty).
     * This is a place where you can update the form UI for a failed request.
     */
    protected abstract void handleBadRequest(@NotNull E fieldErrors);

    /**
     * Runs on UI thread.
     * Guaranteed to be called if there was any error (if there were no field errors, the list will be empty).
     * This is a place where you can update the form UI for a failed request.
     */
    protected abstract void handleOtherClientExceptions(@NotNull SamebugClientException exception);
}
