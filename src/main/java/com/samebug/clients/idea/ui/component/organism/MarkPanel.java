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
package com.samebug.clients.idea.ui.component.organism;

import com.intellij.ide.DataManager;
import com.intellij.openapi.actionSystem.DataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.popup.Balloon;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.ui.awt.RelativePoint;
import com.samebug.clients.common.search.api.entities.RestHit;
import com.samebug.clients.common.ui.Colors;
import com.samebug.clients.idea.components.project.ToolWindowController;
import com.samebug.clients.idea.messages.view.MarkViewListener;
import com.samebug.clients.idea.resources.SamebugBundle;
import com.samebug.clients.idea.resources.SamebugIcons;
import com.samebug.clients.idea.ui.component.ErrorLabel;
import com.samebug.clients.idea.ui.component.SBButton;
import com.samebug.clients.idea.ui.component.TransparentPanel;
import com.samebug.clients.idea.ui.controller.TabController;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

final public class MarkPanel extends TransparentPanel {
    private final SBButton markButton;
    private final JPanel voteIcon;
    private final JLabel helpedLabel;

    @NotNull
    Model model;

    public MarkPanel(@NotNull final Model model) {
        markButton = new MarkButton();
        voteIcon = new VoteIcon();
        helpedLabel = new HelpedLabel();

        this.model = model;

        setLayout(new FlowLayout(FlowLayout.LEFT, 5, 0));
        if (model.canBeMarked()) {
            add(new TransparentPanel() {
                {
                    setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 0));
                    add(markButton);
                }
            });
        }
        add(voteIcon);
        add(helpedLabel);
        updateState();
    }

    public void beginPostMark(@NotNull final Model model) {
        markButton.setEnabled(false);
        this.model = model;
        updateState();
        revalidate();
        repaint();
    }

    public void finishPostMarkWithError(@NotNull final String errorMessage) {
        markButton.setEnabled(true);
        JBPopupFactory.getInstance().createBalloonBuilder(new TransparentPanel() {
            {
                setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
                add(new ErrorLabel(errorMessage));
            }
        }).setFillColor(Colors.alertPanel).createBalloon().show(RelativePoint.getCenterOf(markButton), Balloon.Position.above);
    }

    public void finishPostMarkWithSuccess(@NotNull final Model model) {
        markButton.setEnabled(true);
        this.model = model;
        updateState();
        revalidate();
        repaint();
    }

    private void updateState() {
        final RestHit hit = model.getHit();
        if (hit.getTagged() == null || hit.getTagged()) {
            helpedLabel.setText(SamebugBundle.message("samebug.mark.markedBy.someone", model.createdByCurrentUser() ? "You" : hit.getCreatedBy().getDisplayName(), hit.getScore()));
        } else if (hit.getScore() == 0) {
            helpedLabel.setText(SamebugBundle.message("samebug.mark.markedBy.noone"));
        } else {
            helpedLabel.setText(SamebugBundle.message("samebug.mark.markedBy.anyone", hit.getScore()));
        }
        markButton.setHighlighted(hit.getMarkId() != null);
        if (hit.getMarkId() != null) {
            markButton.setToolTipText(SamebugBundle.message("samebug.mark.marked.tooltip"));
        } else {
            markButton.setToolTipText(SamebugBundle.message("samebug.mark.unmarked.tooltip"));
        }

        for (MouseListener ml : markButton.getMouseListeners()) {
            markButton.removeMouseListener(ml);
        }
        markButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (markButton.isEnabled()) {
                    TabController tab = ToolWindowController.DATA_KEY.getData(DataManager.getInstance().getDataContext(MarkPanel.this));
                    Project project = DataKeys.PROJECT.getData(DataManager.getInstance().getDataContext(MarkPanel.this));
                    beginPostMark(model);
                    if (tab != null && project != null) project.getMessageBus().syncPublisher(MarkViewListener.TOPIC).mark(tab, model);
                }
            }
        });
    }


    private class MarkButton extends SBButton {
        public MarkButton() {
            super("MARK");
        }

        @Override
        public Color getForeground() {
            return Colors.samebugWhite;
        }

        @Override
        public Color getBackground() {
            return Colors.samebugOrange;
        }
    }

    private class VoteIcon extends TransparentPanel {
        static final int width = 16;
        static final int height = 16;

        public VoteIcon() {
            setPreferredSize(new Dimension(width, height));
        }

        @Override
        public void paintComponent(Graphics g) {
            final Icon tickMark = SamebugIcons.tickMark;
            super.paintComponent(g);
            tickMark.paintIcon(MarkPanel.this, g, 0, 0);
        }
    }

    private class HelpedLabel extends JLabel {
    }

    public interface Model {
        @NotNull
        RestHit getHit();

        @NotNull
        int getSearchId();

        boolean canBeMarked();

        boolean createdByCurrentUser();
    }
}
