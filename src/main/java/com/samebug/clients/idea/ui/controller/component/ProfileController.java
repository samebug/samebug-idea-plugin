package com.samebug.clients.idea.ui.controller.component;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.util.messages.MessageBusConnection;
import com.samebug.clients.common.ui.component.profile.IProfilePanel;
import com.samebug.clients.idea.ui.controller.frame.BaseFrameController;
import com.samebug.clients.idea.ui.modules.IdeaListenerService;

public final class ProfileController implements IProfilePanel.Listener {
    final static Logger LOGGER = Logger.getInstance(ProfileController.class);
    final BaseFrameController controller;

    public ProfileController(final BaseFrameController controller) {
        this.controller = controller;

        MessageBusConnection projectConnection = controller.myProject.getMessageBus().connect(controller);
        projectConnection.subscribe(IdeaListenerService.ProfilePanel, this);
    }

    @Override
    public void messagesClicked() {
        controller.twc.focusOnHelpRequestList();
    }
}
