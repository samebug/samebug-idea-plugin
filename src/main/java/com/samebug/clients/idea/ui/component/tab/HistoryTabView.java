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
import com.samebug.clients.idea.components.application.IdeaSamebugPlugin;
import com.samebug.clients.idea.resources.SamebugBundle;
import com.samebug.clients.idea.resources.SamebugIcons;
import com.samebug.clients.idea.ui.component.NetworkStatusIcon;
import com.samebug.clients.idea.ui.component.card.StackTraceSearchGroupCard;
import com.samebug.clients.idea.ui.component.card.TextSearchGroupCard;
import com.samebug.clients.idea.ui.component.organism.WarningPanel;
import com.samebug.clients.search.api.entities.SearchGroup;
import com.samebug.clients.search.api.entities.StackTraceSearchGroup;
import com.samebug.clients.search.api.entities.TextSearchGroup;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;

final public class HistoryTabView extends JPanel {
    @NotNull
    final public JPanel toolbarPanel;
    @NotNull
    final public NetworkStatusIcon statusIcon;
    @NotNull
    JComponent contentPanel;

    public HistoryTabView() {
        statusIcon = new NetworkStatusIcon();
        toolbarPanel = new ToolBarPanel();

        setLayout(new BorderLayout());
        add(toolbarPanel, BorderLayout.NORTH);
        toolbarPanel.add(statusIcon, BorderLayout.EAST);
        setWarningLoading();
    }


    public void setWarningLoading() {
        WarningPanel panel = new WarningPanel(SamebugBundle.message("samebug.toolwindow.search.content.loading"));
        updateContent(panel);
    }

    public void setHistory(@NotNull java.util.List<SearchGroup> groups) {
        final JScrollPane scrollPane = new JScrollPane();
        final JPanel contentPanel = new ContentPanel();

        for (SearchGroup group : groups) {
            if (group instanceof StackTraceSearchGroup) {
                StackTraceSearchGroup g = (StackTraceSearchGroup) group;
                StackTraceSearchGroupCard searchGroupCard = new StackTraceSearchGroupCard(g);
                contentPanel.add(searchGroupCard);
            } else if (group instanceof TextSearchGroup) {
                TextSearchGroup g = (TextSearchGroup) group;
                TextSearchGroupCard searchGroupCard = new TextSearchGroupCard(g);
                contentPanel.add(searchGroupCard);
            }
        }


        updateContent(scrollPane);
        scrollPane.setViewportView(contentPanel);
        scrollPane.getVerticalScrollBar().setUnitIncrement(10);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
    }

    public void setWarningNotLoggedIn() {
        WarningPanel panel = new WarningPanel(SamebugBundle.message("samebug.toolwindow.history.content.notLoggedIn", SamebugIcons.cogwheelTodoUrl));
        updateContent(panel);
    }

    public void setWarningNotConnected() {
        WarningPanel panel =
                new WarningPanel(SamebugBundle.message("samebug.toolwindow.history.content.notConnected", IdeaSamebugPlugin.getInstance().getUrlBuilder().getServerRoot()));
        updateContent(panel);
    }

    public void setWarningNoVisibleSearches() {
        WarningPanel panel = new WarningPanel(SamebugBundle.message("samebug.toolwindow.history.content.noVisibleSearches", SamebugIcons.calendarUrl, SamebugIcons.lightbulbUrl));
        updateContent(panel);
    }

    public void setWarningNoSearches() {
        WarningPanel panel = new WarningPanel(SamebugBundle.message("samebug.toolwindow.history.content.noSearches"));
        updateContent(panel);
    }

    public void setWarningOther() {
        WarningPanel panel = new WarningPanel(SamebugBundle.message("samebug.toolwindow.history.content.other"));
        updateContent(panel);
    }

    void updateContent(@NotNull final JComponent content) {
        if (contentPanel != null) remove(contentPanel);
        contentPanel = content;
        add(contentPanel);
    }

    final class ToolBarPanel extends JPanel {
        {
            setLayout(new BorderLayout());
            setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.black));

            final DefaultActionGroup group = (DefaultActionGroup) ActionManager.getInstance().getAction("Samebug.ToolWindowMenu");
            final ActionToolbar actionToolBar = ActionManager.getInstance().createActionToolbar(ActionPlaces.UNKNOWN, group, true);
            add(actionToolBar.getComponent(), BorderLayout.WEST);
        }
    }

    final class ContentPanel extends JPanel {
        {
            setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        }
    }
}
