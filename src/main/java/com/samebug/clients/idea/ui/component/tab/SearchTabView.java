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
import com.intellij.util.containers.HashMap;
import com.samebug.clients.idea.components.application.IdeaSamebugPlugin;
import com.samebug.clients.idea.resources.SamebugBundle;
import com.samebug.clients.idea.resources.SamebugIcons;
import com.samebug.clients.idea.ui.component.NetworkStatusIcon;
import com.samebug.clients.idea.ui.component.TransparentPanel;
import com.samebug.clients.idea.ui.component.card.*;
import com.samebug.clients.idea.ui.component.organism.MarkPanel;
import com.samebug.clients.idea.ui.component.organism.WarningPanel;
import com.samebug.clients.search.api.entities.SearchGroup;
import com.samebug.clients.search.api.entities.StackTraceSearchGroup;
import com.samebug.clients.search.api.entities.TextSearchGroup;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Map;

final public class SearchTabView extends JPanel {
    @NotNull
    final public JPanel toolbarPanel;
    @NotNull
    final public NetworkStatusIcon statusIcon;
    @NotNull
    JComponent contentPanel;
    @Nullable
    public CollapsibleTipPanel tipPanel;
    @Nullable
    public JPanel header;

    @NotNull
    final Map<Integer, HitView> cards;
    @Nullable
    SearchGroupCard search;

    public SearchTabView() {
        statusIcon = new NetworkStatusIcon();
        toolbarPanel = new ToolBarPanel();
        contentPanel = new TransparentPanel();
        cards = new HashMap<Integer, HitView>();

        setLayout(new BorderLayout());
        add(toolbarPanel, BorderLayout.NORTH);
        toolbarPanel.add(statusIcon, BorderLayout.EAST);
        setWarningLoading();
    }


    public void beginPostMark(int solutionId, @NotNull final MarkPanel.Model model) {
        if (cards.keySet().contains(solutionId)) {
            cards.get(solutionId).markPanel.beginPostMark(model);
        }
    }

    public void finishPostMarkWithError(int solutionId, @NotNull final String errorMessage) {
        if (cards.keySet().contains(solutionId)) {
            cards.get(solutionId).markPanel.finishPostMarkWithError(errorMessage);
        }
    }

    public void finishPostMarkWithSuccess(int solutionId, @NotNull final MarkPanel.Model model) {
        if (cards.keySet().contains(solutionId)) {
            cards.get(solutionId).markPanel.finishPostMarkWithSuccess(model);
        }
    }

    public void beginPostTip() {
        tipPanel.beginPostTip();
    }

    public void finishPostTipWithSuccess() {
        tipPanel.finishPostTipWithSuccess();
    }

    public void finishPostTipWithError(final String message) {
        tipPanel.finishPostTipWithError(message);
    }

    public void setWarningLoading() {
        WarningPanel panel = new WarningPanel(SamebugBundle.message("samebug.toolwindow.search.content.loading"));
        updateContent(panel);
    }

    public void setSolutions(@NotNull final Model model, @NotNull final Actions actions) {
        cards.clear();
        final JPanel controlPanel = new TransparentPanel();
        tipPanel = new CollapsibleTipPanel(actions);

        // add search card to the tipPanel
        if (model.getSearch() instanceof StackTraceSearchGroup) {
            StackTraceSearchGroup group = (StackTraceSearchGroup) model.getSearch();
            search = new StackTraceSearchGroupCard(group, actions);
        } else {
            TextSearchGroup group = (TextSearchGroup) model.getSearch();
            search = new TextSearchGroupCard(group, actions);
        }
        header = new CollapsableView(search,
                SamebugBundle.message("samebug.toolwindow.search.collapsibleHeader.open"), SamebugBundle.message("samebug.toolwindow.search.collapsibleHeader.close"));

        // add result list
        if (model.getTips().isEmpty() && model.getReferences().isEmpty()) {
            // No solutions, show some clarifying message
            WarningPanel panel = new WarningPanel(SamebugBundle.message("samebug.toolwindow.search.content.empty"));
            controlPanel.add(panel);
        } else {
            final JScrollPane scrollPane = new JScrollPane();
            final JPanel solutionsPanel = new SolutionsPanel();

            for (final SamebugTipView.Model tip : model.getTips()) {
                SamebugTipView tipView = new SamebugTipView(tip);
                solutionsPanel.add(tipView);
                cards.put(tip.getHit().getSolutionId(), tipView);
            }
            for (final ExternalSolutionView.Model s : model.getReferences()) {
                final ExternalSolutionView sv = new ExternalSolutionView(s);
                solutionsPanel.add(sv);
                cards.put(s.getHit().getSolutionId(), sv);
            }

            controlPanel.add(scrollPane);
            scrollPane.setViewportView(solutionsPanel);
            scrollPane.getVerticalScrollBar().setUnitIncrement(10);
            scrollPane.setBorder(BorderFactory.createEmptyBorder());
        }
        updateContent(new TransparentPanel() {
            {
                add(new TransparentPanel() {
                    {
                        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
                        add(header);
                        add(tipPanel);
                    }
                }, BorderLayout.NORTH);
                add(controlPanel, BorderLayout.CENTER);
            }
        });

    }

    public void setWarningNotLoggedIn() {
        WarningPanel panel = new WarningPanel(SamebugBundle.message("samebug.toolwindow.search.content.notLoggedIn", SamebugIcons.cogwheelTodoUrl));
        updateContent(panel);
    }

    public void setWarningNotConnected() {
        WarningPanel panel =
                new WarningPanel(SamebugBundle.message("samebug.toolwindow.search.content.notConnected", IdeaSamebugPlugin.getInstance().getUrlBuilder().getServerRoot()));
        updateContent(panel);
    }

    public void setWarningOther() {
        WarningPanel panel = new WarningPanel(SamebugBundle.message("samebug.toolwindow.search.content.other"));
        updateContent(panel);
    }

    public void refreshDateLabels() {
        search.refreshDateLabels();
        for (HitView card : cards.values()) {
            card.refreshDateLabels();
        }
        revalidate();
        repaint();
    }

    void updateContent(final @NotNull JComponent content) {
        remove(contentPanel);
        contentPanel = content;
        add(contentPanel);
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

    public interface Model {
        SearchGroup getSearch();

        List<ExternalSolutionView.Model> getReferences();

        List<SamebugTipView.Model> getTips();
    }

    public interface Actions extends CollapsibleTipPanel.Actions, SearchGroupCard.Actions {

    }
}
