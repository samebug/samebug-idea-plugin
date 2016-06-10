package com.samebug.clients.idea.ui.component;

import com.samebug.clients.idea.components.application.IdeaSamebugPlugin;
import com.samebug.clients.idea.resources.SamebugBundle;
import com.samebug.clients.idea.resources.SamebugIcons;

import javax.swing.*;

public class NetworkStatusIcon extends JLabel {
    public NetworkStatusIcon() {
        setStatusOk();
    }

    public void setStatusOk() {
        setIcon(null);
        setToolTipText(null);
        repaint();
    }

    public void setStatusLoading() {
        setIcon(SamebugIcons.linkActive);
        setToolTipText(SamebugBundle.message("samebug.toolwindow.history.connectionStatus.description.loading"));
        repaint();
    }

    public void setStatusError() {
        setIcon(SamebugIcons.linkError);
        setToolTipText(
                SamebugBundle.message("samebug.toolwindow.history.connectionStatus.description.notConnected",
                        IdeaSamebugPlugin.getInstance().getUrlBuilder().getServerRoot()));
        repaint();
    }

}
