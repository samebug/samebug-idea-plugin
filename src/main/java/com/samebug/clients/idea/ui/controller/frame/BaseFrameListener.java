package com.samebug.clients.idea.ui.controller.frame;

import com.intellij.openapi.options.ShowSettingsUtil;
import com.intellij.openapi.project.Project;
import com.intellij.util.net.HttpProxyConfigurable;
import com.samebug.clients.common.ui.frame.IFrame;
import com.samebug.clients.idea.ui.controller.ConfigDialog;

public abstract class BaseFrameListener implements IFrame.FrameListener {
    private final Project project;

    public BaseFrameListener(Project project) {
        this.project = project;
    }

    @Override
    public void openSamebugSettings() {
        ShowSettingsUtil.getInstance().showSettingsDialog(project, ConfigDialog.class);
    }

    @Override
    public void openNetworkSettings() {
        ShowSettingsUtil.getInstance().showSettingsDialog(project, HttpProxyConfigurable.class);
    }
}
