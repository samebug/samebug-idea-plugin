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
import com.samebug.clients.idea.ui.component.NetworkStatusIcon;
import com.samebug.clients.idea.ui.component.TransparentPanel;
import com.samebug.clients.idea.ui.component.WriteTip;
import com.samebug.clients.idea.ui.component.WriteTipHint;
import com.samebug.clients.idea.ui.component.card.ExternalSolutionView;
import com.samebug.clients.idea.ui.component.card.SamebugTipView;
import com.samebug.clients.idea.ui.component.card.StackTraceSearchGroupCard;
import com.samebug.clients.idea.ui.component.card.TextSearchGroupCard;
import com.samebug.clients.idea.ui.layout.EmptyWarningPanel;
import com.samebug.clients.search.api.entities.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.util.*;
import java.util.List;

final public class SearchTabView extends JPanel {
    @NotNull
    final public JPanel toolbarPanel;
    @NotNull
    final public NetworkStatusIcon statusIcon;

    public SearchTabView() {
        statusIcon = new NetworkStatusIcon();
        toolbarPanel = new ToolBarPanel();

        {
            {
                setLayout(new BorderLayout());
                add(toolbarPanel, BorderLayout.NORTH);
            }
        }

        toolbarPanel.add(statusIcon, BorderLayout.EAST);
    }

    public void reloadImages() {}

    public void setSolutions(@NotNull final Model model) {
        final  JPanel controlPanel = new TransparentPanel();
        final  JPanel header = new TransparentPanel();

        add(new TransparentPanel() {
            {
                add(header, BorderLayout.NORTH);
                add(controlPanel, BorderLayout.CENTER);
            }
        }, BorderLayout.CENTER);

        // add search card to the header
        if (model.getSearch() instanceof StackTraceSearchGroup) {
            StackTraceSearchGroup group = (StackTraceSearchGroup) model.getSearch();
            header.add(new StackTraceSearchGroupCard(group));
        } else {
            TextSearchGroup group = (TextSearchGroup) model.getSearch();
            header.add(new TextSearchGroupCard(group));
        }
        // TODO add tip writing related content?

        if (model.getTips().isEmpty() && model.getReferences().isEmpty()) {
            // No solutions, show some clarifying message
            EmptyWarningPanel panel = new EmptyWarningPanel();
            // TODO bundle
            panel.label.setText("no solution");
            controlPanel.add(panel.controlPanel);
        } else {
            final JScrollPane scrollPane = new JScrollPane();
            final JPanel solutionsPanel = new SolutionsPanel();

            scrollPane.setViewportView(solutionsPanel);
            scrollPane.getVerticalScrollBar().setUnitIncrement(10);
            scrollPane.setBorder(BorderFactory.createEmptyBorder());
            for (final SamebugTipView.Model tip : model.getTips()) {
                SamebugTipView tipView = new SamebugTipView(tip);
                solutionsPanel.add(tipView);
            }
            for (final ExternalSolutionView.Model s : model.getReferences()) {
                final ExternalSolutionView sv = new ExternalSolutionView(s);
                solutionsPanel.add(sv);
            }
        }
    }

    public void setWarningNoSolutions() {}
    public void setWarningNotLoggedIn() {}
    public void setWarningNotConnected() {}
    public void setWarningOther() {}

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
