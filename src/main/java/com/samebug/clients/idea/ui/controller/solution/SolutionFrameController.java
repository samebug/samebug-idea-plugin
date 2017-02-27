package com.samebug.clients.idea.ui.controller.solution;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.options.ShowSettingsUtil;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.util.messages.MessageBusConnection;
import com.intellij.util.net.HttpProxyConfigurable;
import com.samebug.clients.common.ui.component.solutions.ISolutionFrame;
import com.samebug.clients.idea.ui.controller.ConfigDialog;

final class SolutionFrameController implements ISolutionFrame.Listener {
    final static Logger LOGGER = Logger.getInstance(WebHitController.class);
    final SolutionsController controller;

    public SolutionFrameController(final SolutionsController controller) {
        this.controller = controller;

        MessageBusConnection projectConnection = controller.myProject.getMessageBus().connect(controller);
        projectConnection.subscribe(ISolutionFrame.Listener.TOPIC, this);
    }

    @Override
    public void reload() {
        controller.view.setLoading();
        controller.loadAll();
    }

    @Override
    public void openSamebugSettings() {
        ShowSettingsUtil.getInstance().showSettingsDialog(ProjectManager.getInstance().getDefaultProject(), ConfigDialog.class);
    }

    @Override
    public void openNetworkSettings() {
        ShowSettingsUtil.getInstance().showSettingsDialog(ProjectManager.getInstance().getDefaultProject(), HttpProxyConfigurable.class);
    }
}
