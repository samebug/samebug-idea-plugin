/*
 * Copyright 2017 Samebug, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 *    http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.samebug.clients.idea.ui.controller.toolwindow;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.util.messages.MessageBusConnection;
import com.samebug.clients.common.tracking.Funnels;
import com.samebug.clients.common.tracking.Hooks;
import com.samebug.clients.common.ui.modules.TrackingService;
import com.samebug.clients.idea.components.application.ApplicationSettings;
import com.samebug.clients.idea.components.application.IdeaSamebugPlugin;
import com.samebug.clients.swing.tracking.SwingRawEvent;
import com.samebug.util.SBUtil;

public class ConfigChangeListener implements com.samebug.clients.idea.messages.ConfigChangeListener {
    final ToolWindowController twc;

    public ConfigChangeListener(ToolWindowController twc) {
        this.twc = twc;
        final MessageBusConnection connection = ApplicationManager.getApplication().getMessageBus().connect(twc);
        connection.subscribe(ConfigChangeListener.TOPIC, this);
    }

    @Override
    public void configChange(final ApplicationSettings old, final ApplicationSettings c) {
        if (ApplicationManager.getApplication().isDispatchThread()) doConfigChange(old, c);
        else ApplicationManager.getApplication().invokeLater(new Runnable() {
            @Override
            public void run() {
                doConfigChange(old, c);
            }
        });
    }

    private void doConfigChange(ApplicationSettings old, ApplicationSettings c) {
        if (!SBUtil.equals(c.apiKey, old.apiKey) || !SBUtil.equals(c.workspaceId, old.workspaceId)) IdeaSamebugPlugin.getInstance().profileStore.invalidate();

        if (c.apiKey == null) {
            final String authenticationTransactionId = Funnels.newTransactionId();
            TrackingService.trace(SwingRawEvent.authenticationHookTriggered(authenticationTransactionId, Hooks.Authentication.UNAUTHENTICATED));
            twc.focusOnAuthentication(authenticationTransactionId);
        } else {
            if (!SBUtil.equals(c.apiKey, old.apiKey) || !SBUtil.equals(c.serverRoot, old.serverRoot)) twc.focusOnHelpRequestList();
        }
    }
}
