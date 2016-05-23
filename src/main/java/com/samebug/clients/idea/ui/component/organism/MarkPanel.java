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

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.ui.popup.Balloon;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.ui.awt.RelativePoint;
import com.samebug.clients.common.ui.Colors;
import com.samebug.clients.idea.resources.SamebugBundle;
import com.samebug.clients.idea.resources.SamebugIcons;
import com.samebug.clients.idea.ui.component.ErrorLabel;
import com.samebug.clients.idea.ui.component.SBButton;
import com.samebug.clients.idea.ui.component.TransparentPanel;
import com.samebug.clients.search.api.entities.RestHit;
import com.samebug.clients.search.api.entities.SearchGroup;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;

final public class MarkPanel extends TransparentPanel {
    public final SBButton markButton;
    public final JPanel voteIcon;
    public final JLabel helpedLabel;

    final RestHit hit;
    final SearchGroup searchGroup;
    final Integer currentUserId;

    public MarkPanel(@NotNull RestHit hit, @NotNull SearchGroup searchGroup, @NotNull Integer currentUserId) {
        markButton = new MarkButton();
        voteIcon = new VoteIcon();
        helpedLabel = new HelpedLabel();

        this.hit = hit;
        this.searchGroup = searchGroup;
        this.currentUserId = currentUserId;

        setLayout(new FlowLayout(FlowLayout.LEFT, 5, 0));
        final boolean canMark = hit.createdBy == null
                || !hit.createdBy.id.equals(currentUserId)
                // TODO you equal what?
                || !hit.stackId.equals(searchGroup.id);
        if (canMark) {
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

    public void beginPostMark() {
        ApplicationManager.getApplication().assertIsDispatchThread();
        markButton.setEnabled(false);
    }

    public void finishPostMarkWithError(@NotNull final String errorMessage) {
        ApplicationManager.getApplication().assertIsDispatchThread();
        markButton.setEnabled(true);
        JBPopupFactory.getInstance().createBalloonBuilder(new TransparentPanel() {
            {
                setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
                add(new ErrorLabel(errorMessage));
            }
        }).setFillColor(Colors.alertPanel).createBalloon().show(RelativePoint.getCenterOf(markButton), Balloon.Position.above);
    }

    public void finishPostMarkWithSuccess() {
        ApplicationManager.getApplication().assertIsDispatchThread();
        markButton.setEnabled(true);
        updateState();
        revalidate();
        repaint();
    }

    class MarkButton extends SBButton {
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

    class VoteIcon extends TransparentPanel {
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

    class HelpedLabel extends JLabel {
    }

    void updateState() {
        if (hit.createdBy != null) {
            helpedLabel.setText(SamebugBundle.message("samebug.mark.markedBy.someone", hit.createdBy.id.equals(currentUserId) ? "You" : hit.createdBy.displayName, hit.score));
        } else if (hit.score == 0) {
            helpedLabel.setText(SamebugBundle.message("samebug.mark.markedBy.noone"));
        } else {
            helpedLabel.setText(SamebugBundle.message("samebug.mark.markedBy.anyone", hit.score));
        }
        markButton.setHighlighted(hit.markId != null);
        if (hit.markId != null) {
            markButton.setToolTipText(SamebugBundle.message("samebug.mark.marked.tooltip"));
        } else {
            markButton.setToolTipText(SamebugBundle.message("samebug.mark.unmarked.tooltip"));
        }
    }
}
