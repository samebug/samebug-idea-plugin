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
package com.samebug.clients.idea.ui.component.tab;

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
        scrollPane = new JScrollPane();
        contentPanel = new ContentPanel();
        statusIcon = new JLabel();
        toolbarPanel = new ToolBarPanel();

        controlPanel = new JPanel() {
            {
                setLayout(new BorderLayout());

                add(toolbarPanel, BorderLayout.NORTH);
                add(scrollPane, BorderLayout.CENTER);
            }
        };

        statusIcon.setText(null);
        statusIcon.setIcon(null);
        toolbarPanel.add(statusIcon, BorderLayout.EAST);

        scrollPane.setViewportView(contentPanel);
        scrollPane.getVerticalScrollBar().setUnitIncrement(10);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
    }

    class ToolBarPanel extends JPanel {
        {
            setLayout(new BorderLayout());
            setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.black));

            final DefaultActionGroup group = (DefaultActionGroup) ActionManager.getInstance().getAction("Samebug.ToolWindowMenu");
            final ActionToolbar actionToolBar = ActionManager.getInstance().createActionToolbar(ActionPlaces.UNKNOWN, group, true);
            add(actionToolBar.getComponent(), BorderLayout.WEST);
        }
    }

    class ContentPanel extends JPanel {
        {
            setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        }
    }
}
