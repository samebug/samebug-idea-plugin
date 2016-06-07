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
import com.samebug.clients.idea.ui.component.WriteTip;
import com.samebug.clients.idea.ui.component.WriteTipHint;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;

final public class SearchTabView {
    @NotNull
    final public JPanel controlPanel;
    @NotNull
    final public JPanel header;
    @NotNull
    final public JScrollPane scrollPane;
    @NotNull
    final public JPanel solutionsPanel;
    @NotNull
    final public JPanel toolbarPanel;
    @NotNull
    final public JLabel statusIcon;
    @NotNull
    final public WriteTipHint writeTipHint;
    @NotNull
    final public WriteTip tipPanel;

    // TODO searchCard is set by the controller, refactor
    @Nullable
    public JPanel searchCard;

    public SearchTabView() {

        header = new JPanel() {
            {
                setLayout(new BorderLayout());
                setBorder(BorderFactory.createEmptyBorder());
            }
        };
        scrollPane = new JScrollPane();
        solutionsPanel = new SolutionsPanel();
        scrollPane.setViewportView(solutionsPanel);
        scrollPane.getVerticalScrollBar().setUnitIncrement(10);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        statusIcon = new JLabel();
        statusIcon.setText(null);
        statusIcon.setIcon(null);
        toolbarPanel = new ToolBarPanel();
        toolbarPanel.add(statusIcon, BorderLayout.EAST);

        controlPanel = new JPanel() {
            {
                setLayout(new BorderLayout());
                add(new TransparentPanel() {
                    {
                        add(toolbarPanel, BorderLayout.NORTH);
                        add(header, BorderLayout.CENTER);
                    }
                }, BorderLayout.NORTH);
                add(scrollPane, BorderLayout.CENTER);
            }
        };

        writeTipHint = new WriteTipHint();
        tipPanel = new WriteTip();
    }

    public void showWriteTip() {
        header.removeAll();
        header.add(new TransparentPanel() {
            {
                add(searchCard, BorderLayout.CENTER);
                add(tipPanel, BorderLayout.SOUTH);
                setPreferredSize(new Dimension(getPreferredSize().width, Math.min(getPreferredSize().height, 167 + tipPanel.getPreferredSize().height)));
            }
        });
    }

    public void showWriteTipHint() {
        header.removeAll();
        header.add(new TransparentPanel() {
            {
                add(searchCard, BorderLayout.CENTER);
                add(writeTipHint, BorderLayout.SOUTH);
                setPreferredSize(new Dimension(getPreferredSize().width, Math.min(getPreferredSize().height, 167 + writeTipHint.getPreferredSize().height)));
            }
        });
    }

    final class ToolBarPanel extends JPanel {
        {
            setLayout(new BorderLayout());
            setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.black));

            final DefaultActionGroup group = (DefaultActionGroup) ActionManager.getInstance().getAction("Samebug.SearchTabMenu");
            final ActionToolbar actionToolBar = ActionManager.getInstance().createActionToolbar(ActionPlaces.UNKNOWN, group, true);
            add(actionToolBar.getComponent(), BorderLayout.WEST);
        }
    }

    final class SolutionsPanel extends JPanel {
        {
            setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        }
    }
}
