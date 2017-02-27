package com.samebug.clients.idea.ui.controller.solution;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.util.messages.MessageBusConnection;
import com.samebug.clients.common.ui.component.solutions.IHelpOthersCTA;

final class HelpOthersCTAController implements IHelpOthersCTA.Listener {
    final static Logger LOGGER = Logger.getInstance(HelpOthersCTAController.class);
    final SolutionsController controller;

    public HelpOthersCTAController(final SolutionsController controller) {
        this.controller = controller;

        MessageBusConnection projectConnection = controller.myProject.getMessageBus().connect(controller);
        projectConnection.subscribe(IHelpOthersCTA.Listener.TOPIC, this);
    }

    @Override
    public void ctaClicked(IHelpOthersCTA source) {
        LOGGER.debug("help others cta clicked");
    }
}
