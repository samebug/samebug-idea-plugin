/**
 * Copyright 2017 Samebug, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
