package com.samebug.clients.idea.ui.controller;

import com.intellij.openapi.Disposable;
import com.intellij.util.messages.MessageBus;
import com.intellij.util.messages.MessageBusConnection;
import com.samebug.clients.common.search.api.client.ConnectionStatus;
import com.samebug.clients.common.services.ClientService;
import com.samebug.clients.common.ui.component.util.IFrame;
import com.samebug.clients.idea.components.application.IdeaSamebugPlugin;

public final class ConnectionStatusController implements ClientService.ConnectionStatusListener, Disposable {
    final IFrame frame;

    public ConnectionStatusController(IFrame frame, MessageBus messageBus) {
        this.frame = frame;

        MessageBusConnection projectConnection = messageBus.connect(this);
        projectConnection.subscribe(ClientService.ConnectionStatusListener.TOPIC, this);

        // initialize the error bars if necessary
        ClientService clientService = IdeaSamebugPlugin.getInstance().clientService;
        if (!clientService.isConnected()) connectionChange(false);
        else if (!clientService.isAuthenticated()) authenticationChange(false);
    }

    @Override
    public void startRequest() {

    }

    @Override
    public void finishRequest(ConnectionStatus status) {

    }

    @Override
    public void connectionChange(boolean isConnected) {
        if (!isConnected) frame.showNetworkError();
        else frame.hideNetworkError();
    }

    @Override
    public void authenticationChange(boolean isAuthenticated) {
        ClientService clientService = IdeaSamebugPlugin.getInstance().clientService;
        // NOTE being unauthenticated is only a problem when we are at least connected to the server
        if (clientService.isConnected()) {
            if (!isAuthenticated) frame.showAuthenticationError();
            else frame.hideAuthenticationError();
        }
    }

    @Override
    public void apiToBeDeprecated() {

    }

    @Override
    public void apiDeprecated() {

    }

    @Override
    public void dispose() {

    }
}
