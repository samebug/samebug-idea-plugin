package com.samebug.clients.idea.ui.controller.helpRequestList;

import com.intellij.util.messages.MessageBusConnection;
import com.samebug.clients.common.ui.frame.helpRequestList.IHelpRequestListFrame;
import com.samebug.clients.idea.ui.controller.frame.BaseFrameListener;
import com.samebug.clients.idea.ui.modules.IdeaListenerService;

public final class HelpRequestListListener extends BaseFrameListener implements IHelpRequestListFrame.Listener {
    final HelpRequestListController controller;

    public HelpRequestListListener(final HelpRequestListController controller) {
        super(controller.myProject);
        this.controller = controller;

        MessageBusConnection projectConnection = controller.myProject.getMessageBus().connect(controller);
        projectConnection.subscribe(IdeaListenerService.HelpRequestListFrame, this);
    }

    @Override
    public void reload() {
        controller.load();
    }
}