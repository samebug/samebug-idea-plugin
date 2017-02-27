package com.samebug.clients.idea.ui.controller.solution;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.util.messages.MessageBusConnection;
import com.samebug.clients.common.ui.component.solutions.IBugmateList;

final class BugmateListController implements IBugmateList.Listener {
    final static Logger LOGGER = Logger.getInstance(BugmateListController.class);

    final SolutionsController controller;

    public BugmateListController(final SolutionsController controller) {
        this.controller = controller;

        MessageBusConnection projectConnection = controller.myProject.getMessageBus().connect(controller);
        projectConnection.subscribe(IBugmateList.Listener.TOPIC, this);
    }


    @Override
    public void askBugmates(IBugmateList source) {
        LOGGER.debug("ask bugmates clicked");
    }
}
