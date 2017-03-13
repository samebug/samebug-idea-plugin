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
package com.samebug.clients.idea.ui.controller.frame;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.util.messages.MessageBus;
import com.samebug.clients.common.api.exceptions.*;
import com.samebug.clients.common.ui.frame.IFrame;
import com.samebug.clients.idea.components.application.IdeaSamebugPlugin;
import com.samebug.clients.idea.components.project.ToolWindowController;
import com.samebug.clients.idea.ui.modules.IdeaDataService;
import com.samebug.clients.swing.ui.modules.DataService;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.concurrent.ExecutionException;

public abstract class BaseFrameController<T extends IFrame> implements Disposable {
    final static Logger LOGGER = Logger.getInstance(BaseFrameController.class);

    public final ToolWindowController twc;
    public final Project myProject;
    public final T view;

    public final ConnectionStatusController connectionStatusController;
    public final ConversionService conversionService;
    public final ConcurrencyService concurrencyService;

    public BaseFrameController(ToolWindowController twc, Project project, T view) {
        this.twc = twc;
        this.myProject = project;
        this.view = view;

        MessageBus messageBus = myProject.getMessageBus();
        connectionStatusController = new ConnectionStatusController(view, messageBus);

        // TODO this is a bit fragile, but forgetting to set the project of the component in the derived component is even more fragile
        DataService.putData((JComponent) view, IdeaDataService.Project, project);

        IdeaSamebugPlugin plugin = IdeaSamebugPlugin.getInstance();
        conversionService = plugin.conversionService;
        concurrencyService = plugin.concurrencyService;
    }

    protected abstract class LoadingTask {
        protected abstract void load() throws java.lang.Exception;

        public void executeInBackground() {
            ApplicationManager.getApplication().executeOnPooledThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        try {
                            load();
                        } catch (InterruptedException e) {
                            handleInterruptedException(e);
                        } catch (ExecutionException e) {
                            handleExecutionException(e);
                        }
                    } catch (final SamebugClientException e) {
                        handleSamebugClientException(e);
                    } catch (final Exception e) {
                        handleOtherException(e);
                    }
                }
            });
        }


        protected void handleInterruptedException(InterruptedException e) {
            // TODO not sure when could it happen, probably safe to retry
            LOGGER.warn("Loading interrupted", e);
            ApplicationManager.getApplication().invokeLater(new Runnable() {
                @Override
                public void run() {
                    view.loadingFailedWithRetriableError();
                }
            });
        }

        protected void handleExecutionException(ExecutionException e) throws SamebugClientException {
            if (e.getCause() instanceof SamebugClientException) throw (SamebugClientException) e.getCause();
            else {
                // some of the executed tasks failed with an exception, that means a bug in the plugin
                LOGGER.warn("Plugin-side error during loading", e);
                ApplicationManager.getApplication().invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        view.loadingFailedWithGenericError();
                    }
                });
            }
        }

        protected void handleSamebugClientException(final SamebugClientException e) {
            // TODO error with loading, bad connection, bad apikey, server error, etc
            LOGGER.warn("Error during loading solutions", e);
            ApplicationManager.getApplication().invokeLater(new Runnable() {
                @Override
                public void run() {
                    if (e instanceof SamebugTimeout) view.loadingFailedWithRetriableError();
                    else if (e instanceof UserUnauthenticated) view.loadingFailedWithAuthenticationError();
                    else if (e instanceof UserUnauthorized) view.loadingFailedWithAuthorizationError();
                    else if (e instanceof UnsuccessfulResponseStatus && ((UnsuccessfulResponseStatus) e).statusCode == 500) view.loadingFailedWithServerError();
                    else if (e instanceof HttpError) view.loadingFailedWithNetworkError();
                    else view.loadingFailedWithGenericError();
                }
            });
        }

        protected void handleOtherException(java.lang.Exception e) {
            // anything can go wrong during the loading (e.g. missing json field)
            LOGGER.warn("Unexpected exception during loading", e);
            ApplicationManager.getApplication().invokeLater(new Runnable() {
                @Override
                public void run() {
                    view.loadingFailedWithGenericError();
                }
            });
        }
    }

    @NotNull
    public JComponent getControlPanel() {
        return (JComponent) view;
    }

    @Override
    public void dispose() {
        connectionStatusController.dispose();
    }
}
