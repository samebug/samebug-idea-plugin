package com.samebug.clients.idea.ui.controller.helpRequest;

import com.intellij.openapi.project.Project;
import com.intellij.util.messages.MessageBusConnection;
import com.samebug.clients.common.ui.frame.helpRequest.IHelpRequestFrame;
import com.samebug.clients.common.ui.frame.solution.ISolutionFrame;
import com.samebug.clients.idea.ui.controller.frame.BaseFrameListener;
import com.samebug.clients.idea.ui.modules.IdeaListenerService;

public final class HelpRequestFrameListener extends BaseFrameListener implements IHelpRequestFrame.Listener{
    final HelpRequestController controller;

    public HelpRequestFrameListener(HelpRequestController controller) {
        super(controller.myProject);
        this.controller = controller;

        MessageBusConnection projectConnection = controller.myProject.getMessageBus().connect(controller);
        projectConnection.subscribe(IdeaListenerService.HelpRequestFrame, this);
    }

    @Override
    public void reload() {
        controller.load();
    }
}
