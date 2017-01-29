/**
 * Copyright 2017 Samebug, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.samebug.clients.idea.services;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.MessageType;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.util.messages.MessageBusConnection;
import com.samebug.clients.common.search.api.client.ConnectionStatus;
import com.samebug.clients.idea.messages.model.ConnectionStatusListener;
import com.samebug.clients.idea.resources.SamebugBundle;

final public class ApiService implements ConnectionStatusListener {
    private final Project myProject;

    public ApiService(Project project) {
        myProject = project;
    }

    public void projectOpened() {
        MessageBusConnection projectConnection = myProject.getMessageBus().connect(myProject);
        projectConnection.subscribe(ConnectionStatusListener.TOPIC, this);
    }

    public void projectClosed() {

    }

    @Override
    public void startRequest() {

    }

    @Override
    public void connectionChange(boolean isConnected) {

    }

    @Override
    public void authenticationChange(boolean isAuthenticated) {

    }

    @Override
    public void apiToBeDeprecated() {
        ApplicationManager.getApplication().invokeLater(new Runnable() {
            @Override
            public void run() {
                ToolWindowManager.getInstance(myProject).notifyByBalloon(
                        "Samebug",
                        MessageType.WARNING,
                        SamebugBundle.message("samebug.notification.apiStatus.toBeDeprecated"),
                        Messages.getWarningIcon(),
                        null);
            }
        });
    }

    @Override
    public void apiDeprecated() {
        ApplicationManager.getApplication().invokeLater(new Runnable() {
            @Override
            public void run() {
                ToolWindowManager.getInstance(myProject).notifyByBalloon(
                        "Samebug",
                        MessageType.ERROR, SamebugBundle.message("samebug.notification.apiStatus.deprecated"),
                        Messages.getErrorIcon(),
                        null);
            }
        });
    }

    @Override
    public void finishRequest(ConnectionStatus status) {

    }
}
