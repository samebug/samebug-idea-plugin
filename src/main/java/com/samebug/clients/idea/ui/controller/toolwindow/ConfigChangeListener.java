package com.samebug.clients.idea.ui.controller.toolwindow;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.util.messages.MessageBusConnection;
import com.samebug.clients.idea.components.application.ApplicationSettings;

public class ConfigChangeListener implements com.samebug.clients.idea.messages.ConfigChangeListener {
    final ToolWindowController twc;

    public ConfigChangeListener(ToolWindowController twc) {
        this.twc = twc;
        final MessageBusConnection connection = ApplicationManager.getApplication().getMessageBus().connect(twc);
        connection.subscribe(ConfigChangeListener.TOPIC, this);
    }

    @Override
    public void configChange(ApplicationSettings old, ApplicationSettings c) {
        if (c.apiKey == null) twc.focusOnAuthentication();
        else {
            if (!c.apiKey.equals(old.apiKey) || !c.serverRoot.equals(old.serverRoot)) twc.focusOnHelpRequestList();
        }
    }
}
