/**
 * Copyright 2016 Samebug, Inc.
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
package com.samebug.clients.idea.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.samebug.clients.idea.components.application.IdeaSamebugPlugin;
import com.samebug.clients.idea.messages.ConnectionStatusListener;
import com.samebug.clients.idea.resources.SamebugIcons;
import com.samebug.clients.idea.ui.SettingsDialog;

public class SettingsAction extends AnAction implements ConnectionStatusListener {
    private boolean connected = true;
    private boolean authorized = true;
    private int nRequests = 0;

    @Override
    public void actionPerformed(AnActionEvent e) {
        IdeaSamebugPlugin plugin = IdeaSamebugPlugin.getInstance();
        SettingsDialog.setup(plugin.getApiKey());
    }

    @Override
    public void update(AnActionEvent e) {
        if (nRequests > 0) {
          e.getPresentation().setIcon(SamebugIcons.statusWorking);
        } else if (connected && authorized) {
            e.getPresentation().setText("You are connected with a valid api key");
            e.getPresentation().setIcon(SamebugIcons.statusOk);
        }
        else if (!connected) {
            e.getPresentation().setText("Cannot connect to samebug.io");
            e.getPresentation().setIcon(SamebugIcons.statusNotConnected);
        }
        else {
            e.getPresentation().setText("It seems your api key is not valid");
            e.getPresentation().setIcon(SamebugIcons.statusInvalidApiKey);
        }
    }

    @Override
    public synchronized void startRequest() {
        ++nRequests;
    }

    @Override
    public synchronized void finishRequest(boolean isConnected) {
        --nRequests;
        this.connected = isConnected;
    }

    @Override
    public synchronized void authorizationChange(boolean isAuthorized) {
        this.authorized = isAuthorized;
    }
}
