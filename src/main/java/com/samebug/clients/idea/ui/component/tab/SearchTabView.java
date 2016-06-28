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
    @NotNull
    final Map<Integer, HitView> cards;

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


    @Nullable
    public HitView getHitCard(int solutionId) {
        if (cards.keySet().contains(solutionId)) return cards.get(solutionId);
        else return null;
    }

    public void reloadImages() {
    }

    public void setWarningLoading() {
        WarningPanel panel = new WarningPanel(SamebugBundle.message("samebug.toolwindow.search.content.loading"));
        updateContent(panel);
    }

    public void setSolutions(@NotNull final Model model) {
        cards.clear();
        final JPanel controlPanel = new TransparentPanel();
        final JPanel header = new TransparentPanel();

        updateContent(new TransparentPanel() {
            {
                add(header, BorderLayout.NORTH);
                add(controlPanel, BorderLayout.CENTER);
            }
        });

        // add search card to the header
        if (model.getSearch() instanceof StackTraceSearchGroup) {
            StackTraceSearchGroup group = (StackTraceSearchGroup) model.getSearch();
            header.add(new StackTraceSearchGroupCard(group));
        } else {
            TextSearchGroup group = (TextSearchGroup) model.getSearch();
            header.add(new TextSearchGroupCard(group));
        }
        // TODO add tip writing related content?

        // add result list
        if (model.getTips().isEmpty() && model.getReferences().isEmpty()) {
            // No solutions, show some clarifying message
            WarningPanel panel = new WarningPanel(SamebugBundle.message("samebug.toolwindow.search.content.empty"));
            controlPanel.add(panel);
        } else {
            final JScrollPane scrollPane = new JScrollPane();
            final JPanel solutionsPanel = new SolutionsPanel();

            controlPanel.add(scrollPane);
            scrollPane.setViewportView(solutionsPanel);
            scrollPane.getVerticalScrollBar().setUnitIncrement(10);
            scrollPane.setBorder(BorderFactory.createEmptyBorder());
            for (final SamebugTipView.Model tip : model.getTips()) {
                SamebugTipView tipView = new SamebugTipView(tip);
                solutionsPanel.add(tipView);
                cards.put(tip.getHit().solutionId, tipView);
            }
            for (final ExternalSolutionView.Model s : model.getReferences()) {
                final ExternalSolutionView sv = new ExternalSolutionView(s);
                solutionsPanel.add(sv);
                cards.put(s.getHit().solutionId, sv);
            }
        }
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

//    public void showWriteTip() {
//        header.removeAll();
//        header.add(new TransparentPanel() {
//            {
//                add(searchCard, BorderLayout.CENTER);
//                add(tipPanel, BorderLayout.SOUTH);
//                setPreferredSize(new Dimension(getPreferredSize().width, Math.min(getPreferredSize().height, 167 + tipPanel.getPreferredSize().height)));
//            }
//        });
//    }
//
//    public void showWriteTipHint() {
//        header.removeAll();
//        header.add(new TransparentPanel() {
//            {
//                add(searchCard, BorderLayout.CENTER);
//                add(writeTipHint, BorderLayout.SOUTH);
//                setPreferredSize(new Dimension(getPreferredSize().width, Math.min(getPreferredSize().height, 167 + writeTipHint.getPreferredSize().height)));
//            }
//        });
//    }
//
//

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
}
