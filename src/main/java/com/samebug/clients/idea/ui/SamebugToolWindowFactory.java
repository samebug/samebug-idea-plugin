/**
 * Copyright 2016 Samebug, Inc.
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.samebug.clients.idea.ui;

import com.intellij.application.options.colors.ColorSettingsUtil;
import com.intellij.ide.ui.AppearanceConfigurable;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import com.intellij.util.messages.MessageBusConnection;
import com.intellij.util.ui.UIUtil;
import com.samebug.clients.idea.actions.HistoryAction;
import com.samebug.clients.idea.messages.BatchStackTraceSearchListener;
import com.samebug.clients.idea.resources.SamebugBundle;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;


public class SamebugToolWindowFactory implements ToolWindowFactory, DumbAware {

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        SamebugHistoryWindow historyWindow = initializeHistoryWindow(project);
        ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
        Content content = contentFactory.createContent(historyWindow.getControlPanel(), SamebugBundle.message("samebug.toolwindow.displayName"), false);
        toolWindow.getContentManager().addContent(content);
    }

    private SamebugHistoryWindow initializeHistoryWindow(Project project) {
        SamebugHistoryWindow historyWindow = new SamebugHistoryWindow(project);

        historyWindow.initHistoryPane();

        final HistoryAction historyAction = (HistoryAction) ActionManager.getInstance().getAction("Samebug.History");
        historyAction.setHook(historyWindow);

        MessageBusConnection messageBusConnection = project.getMessageBus().connect(project);
        messageBusConnection.subscribe(BatchStackTraceSearchListener.BATCH_SEARCH_TOPIC, historyWindow);

        return historyWindow;
    }
}
