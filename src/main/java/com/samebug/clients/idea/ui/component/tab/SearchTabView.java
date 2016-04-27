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
import com.samebug.clients.idea.ui.component.TransparentPanel;
import com.samebug.clients.idea.ui.component.card.SearchGroupCardView;

import javax.swing.*;
import java.awt.*;

final public class SearchTabView {
    public JPanel controlPanel;
    public JPanel header;
    public JScrollPane scrollPane;
    public JPanel solutionsPanel;
    public JPanel toolbarPanel;
    public JLabel statusIcon;

    public SearchTabView() {

        header = new JPanel() {
            {
                setLayout(new BorderLayout());
                setBorder(BorderFactory.createEmptyBorder());
            }
        };
        scrollPane = new JScrollPane();
        solutionsPanel = new SolutionsPanel();
        controlPanel = new JPanel() {
            {
                setLayout(new BorderLayout());
                add(header, BorderLayout.NORTH);
                add(scrollPane, BorderLayout.CENTER);
            }
        };

        scrollPane.setViewportView(solutionsPanel);
        scrollPane.getVerticalScrollBar().setUnitIncrement(10);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        statusIcon = new JLabel();
        statusIcon.setText(null);
        statusIcon.setIcon(null);
        toolbarPanel = new ToolBarPanel();
        toolbarPanel.add(statusIcon, BorderLayout.EAST);
    }

    public void makeHeader(final SearchGroupCardView search, final JComponent extension) {
        header.removeAll();
        header.add(new TransparentPanel() {
            {
                add(toolbarPanel, BorderLayout.NORTH);
                if (search != null) {
                    add(search, BorderLayout.CENTER);
                    if (extension != null) {
                        add(extension, BorderLayout.SOUTH);
                        setPreferredSize(new Dimension(getPreferredSize().width, Math.min(getPreferredSize().height, 167 + extension.getPreferredSize().height)));
                    } else {
                        setPreferredSize(new Dimension(getPreferredSize().width, Math.min(getPreferredSize().height, 167)));
                    }
                }
            }
        });
    }

    class ToolBarPanel extends JPanel {
        {
            setLayout(new BorderLayout());
            setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.black));

            final DefaultActionGroup group = (DefaultActionGroup) ActionManager.getInstance().getAction("Samebug.SearchTabMenu");
            final ActionToolbar actionToolBar = ActionManager.getInstance().createActionToolbar(ActionPlaces.UNKNOWN, group, true);
            add(actionToolBar.getComponent(), BorderLayout.WEST);
        }
    }

    class SolutionsPanel extends JPanel {
        {
            setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        }
    }
}
