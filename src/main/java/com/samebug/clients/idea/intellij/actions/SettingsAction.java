package com.samebug.clients.idea.intellij.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.samebug.clients.idea.SamebugIdeaPlugin;
import com.samebug.clients.idea.intellij.settings.SettingsDialog;
import com.samebug.clients.idea.messages.SamebugBundle;
import com.samebug.clients.idea.messages.SamebugIcons;

public class SettingsAction extends AnAction {
    public SettingsAction() {
        super(SamebugBundle.message("samebug.toolwindow.toolbar.actions.settings.text"), SamebugBundle.message("samebug.toolwindow.toolbar.actions.settings.description"), SamebugIcons.settingsIcon);
    }

    @Override
    public void actionPerformed(AnActionEvent e) {
        SettingsDialog.setup(SamebugIdeaPlugin.getInstance());
    }
}
