package com.samebug.clients.idea.ui.controller.solution;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.util.messages.MessageBusConnection;
import com.samebug.clients.common.ui.component.solutions.IWebHit;
import com.samebug.clients.idea.ui.BrowserUtil;

import java.net.URL;

final class WebHitController implements IWebHit.Listener {
    final static Logger LOGGER = Logger.getInstance(WebHitController.class);
    final SolutionFrameController controller;

    public WebHitController(final SolutionFrameController controller) {
        this.controller = controller;

        MessageBusConnection projectConnection = controller.myProject.getMessageBus().connect(controller);
        projectConnection.subscribe(IWebHit.Listener.TOPIC, this);
    }

    @Override
    public void urlClicked(URL url) {
        BrowserUtil.browse(url);
    }
}
