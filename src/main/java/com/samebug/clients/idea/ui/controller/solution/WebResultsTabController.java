package com.samebug.clients.idea.ui.controller.solution;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.util.messages.MessageBusConnection;
import com.samebug.clients.common.ui.component.solutions.IWebResultsTab;

final class WebResultsTabController implements IWebResultsTab.Listener {
    final static Logger LOGGER = Logger.getInstance(WebResultsTabController.class);
    final SolutionsController controller;

    public WebResultsTabController(final SolutionsController controller) {
        this.controller = controller;

        MessageBusConnection projectConnection = controller.myProject.getMessageBus().connect(controller);
        projectConnection.subscribe(IWebResultsTab.Listener.TOPIC, this);
    }


    @Override
    public void moreClicked() {
        LOGGER.debug("more button clicked");
    }
}
