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
package com.samebug.clients.idea.intellij.actions;

import com.intellij.ide.BrowserUtil;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.samebug.clients.idea.SamebugIdeaPlugin;
import com.samebug.clients.rest.SamebugClient;

public class HistoryAction extends AnAction {
    @Override
    public void actionPerformed(AnActionEvent e) {
        final SamebugIdeaPlugin plugin = SamebugIdeaPlugin.getInstance();
        final Integer userId = plugin.getState().getUserId();
        final SamebugClient client = SamebugIdeaPlugin.getClient();
        if (userId != null) {
            BrowserUtil.browse(client.getUserProfileUrl(userId));
        }
    }
}
