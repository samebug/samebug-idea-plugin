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
package com.samebug.clients.idea.ui.views;

import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.ActionPlaces;
import com.intellij.openapi.actionSystem.ActionToolbar;
import com.intellij.openapi.actionSystem.DefaultActionGroup;

import javax.swing.*;
import java.awt.*;

/**
 * Created by poroszd on 3/4/16.
 */
public class HistoryTabView {
    public JPanel controlPanel;
    public JPanel toolbarPanel;
    public JLabel statusIcon;
    public JScrollPane scrollPane;
    public JPanel contentPanel;

    public HistoryTabView() {
        controlPanel = new JPanel();
        controlPanel.setLayout(new BorderLayout(0, 0));
        toolbarPanel = new JPanel();
        toolbarPanel.setLayout(new BorderLayout(0, 0));
        controlPanel.add(toolbarPanel, BorderLayout.NORTH);
        scrollPane = new JScrollPane();
        controlPanel.add(scrollPane, BorderLayout.CENTER);
        contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.PAGE_AXIS));
        scrollPane.setViewportView(contentPanel);
        scrollPane.getVerticalScrollBar().setUnitIncrement(10);
        statusIcon = new JLabel();
        statusIcon.setText(null);
        statusIcon.setIcon(null);

        final DefaultActionGroup group = (DefaultActionGroup) ActionManager.getInstance().getAction("Samebug.ToolWindowMenu");
        final ActionToolbar actionToolBar = ActionManager.getInstance().createActionToolbar(ActionPlaces.UNKNOWN, group, true);
        toolbarPanel.add(actionToolBar.getComponent(), BorderLayout.WEST);
        toolbarPanel.add(statusIcon, BorderLayout.EAST);
    }
}
