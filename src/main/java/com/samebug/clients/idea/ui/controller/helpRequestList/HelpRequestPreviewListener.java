package com.samebug.clients.idea.ui.controller.helpRequestList;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.util.messages.MessageBusConnection;
import com.samebug.clients.common.ui.component.helpRequest.IHelpRequestPreview;
import com.samebug.clients.idea.ui.modules.IdeaListenerService;

final class HelpRequestPreviewListener implements IHelpRequestPreview.Listener {
    final static Logger LOGGER = Logger.getInstance(HelpRequestPreviewListener.class);

    final HelpRequestListController controller;

    HelpRequestPreviewListener(HelpRequestListController controller) {
        this.controller = controller;

        MessageBusConnection projectConnection = controller.myProject.getMessageBus().connect(controller);
        projectConnection.subscribe(IdeaListenerService.HelpRequestPreview, this);
    }


    @Override
    public void previewClicked(IHelpRequestPreview source, String helpRequestId) {
        controller.twc.focusOnHelpRequest(helpRequestId);
    }
}
