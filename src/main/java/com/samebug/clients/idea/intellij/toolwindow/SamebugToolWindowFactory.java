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
package com.samebug.clients.idea.intellij.toolwindow;

import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import com.samebug.clients.idea.intellij.actions.SettingsAction;
import com.samebug.clients.idea.messages.SamebugBundle;
import javax.annotation.Nonnull;

import javax.swing.*;
import java.awt.*;

public class SamebugToolWindowFactory implements ToolWindowFactory {
    private JPanel contentPanel;
    private JPanel toolbarPanel;
    private ToolWindow toolWindow;
    private Project project;

    @Override
    public void createToolWindowContent(@Nonnull Project project, @Nonnull ToolWindow toolWindow) {
        this.project = project;
        this.toolWindow = toolWindow;


        initContent();
    }

    private void initContent() {
        ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
        Content content = contentFactory.createContent(contentPanel, SamebugBundle.message("samebug.toolwindow.displayName"), false);

        toolWindow.getContentManager().addContent(content);
    }


    private final static Logger LOGGER = Logger.getInstance(SamebugToolWindowFactory.class);

    private void createUIComponents() {
        this.toolbarPanel = createToolbarPanel();
    }

    private JPanel createToolbarPanel() {
        final DefaultActionGroup group = new DefaultActionGroup();

        group.add(new SettingsAction());

        final ActionToolbar actionToolBar = ActionManager.getInstance().createActionToolbar(ActionPlaces.UNKNOWN, group, true);
        final JPanel buttonsPanel = new JPanel(new BorderLayout());
        buttonsPanel.add(actionToolBar.getComponent(), BorderLayout.CENTER);
        return buttonsPanel;
    }
}
