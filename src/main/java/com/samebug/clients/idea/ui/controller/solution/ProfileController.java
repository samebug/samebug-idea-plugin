package com.samebug.clients.idea.ui.controller.solution;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.util.messages.MessageBusConnection;
import com.samebug.clients.common.ui.component.profile.IProfilePanel;

final class ProfileController implements IProfilePanel.Listener {
    final static Logger LOGGER = Logger.getInstance(ProfileController.class);
    final SolutionsController controller;

    public ProfileController(final SolutionsController controller) {
        this.controller = controller;

        MessageBusConnection projectConnection = controller.myProject.getMessageBus().connect(controller);
        projectConnection.subscribe(IProfilePanel.Listener.TOPIC, this);
    }

    @Override
    public void messagesClicked() {
        LOGGER.debug("messages clicked");
    }
}
