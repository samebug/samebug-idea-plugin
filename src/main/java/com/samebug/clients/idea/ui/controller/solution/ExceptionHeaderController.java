package com.samebug.clients.idea.ui.controller.solution;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.util.messages.MessageBusConnection;
import com.samebug.clients.common.ui.component.solutions.IExceptionHeaderPanel;
import com.samebug.clients.idea.components.application.IdeaSamebugPlugin;
import com.samebug.clients.idea.ui.BrowserUtil;

import java.net.URL;

final class ExceptionHeaderController implements IExceptionHeaderPanel.Listener {
    final static Logger LOGGER = Logger.getInstance(ExceptionHeaderController.class);

    final SolutionFrameController controller;

    public ExceptionHeaderController(final SolutionFrameController controller) {
        this.controller = controller;

        MessageBusConnection projectConnection = controller.myProject.getMessageBus().connect(controller);
        projectConnection.subscribe(IExceptionHeaderPanel.Listener.TOPIC, this);
    }

    @Override
    public void titleClicked() {
        final URL searchUrl = IdeaSamebugPlugin.getInstance().urlBuilder.search(controller.searchId);
        BrowserUtil.browse(searchUrl);
    }
}