package com.samebug.clients.idea.components.project;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.MessageType;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.util.messages.MessageBusConnection;
import com.samebug.clients.common.search.api.client.ConnectionStatus;
import com.samebug.clients.common.services.ClientService;
import com.samebug.clients.swing.ui.SamebugBundle;

public final class DeprecationNotifier implements ClientService.ConnectionStatusListener {
    private final Project myProject;

    public DeprecationNotifier(Project project) {
        myProject = project;
        MessageBusConnection projectConnection = myProject.getMessageBus().connect(project);
        projectConnection.subscribe(ClientService.ConnectionStatusListener.TOPIC, this);
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
