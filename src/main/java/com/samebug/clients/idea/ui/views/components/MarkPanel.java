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
package com.samebug.clients.idea.ui.views.components;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.ui.popup.Balloon;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.ui.awt.RelativePoint;
import com.samebug.clients.idea.components.application.IdeaSamebugPlugin;
import com.samebug.clients.idea.resources.SamebugBundle;
import com.samebug.clients.idea.resources.SamebugIcons;
import com.samebug.clients.idea.ui.Colors;
import com.samebug.clients.search.api.entities.legacy.UserReference;

import javax.swing.*;
import java.awt.*;
import java.awt.font.TextAttribute;
import java.util.HashMap;

/**
 * Created by poroszd on 4/8/16.
 */
public class MarkPanel extends TransparentPanel {
    public final SBButton markButton;
    public final JPanel voteIcon;
    public final JLabel helpedLabel;

    final UserReference createdBy;
    final Integer currentUserId;

    public MarkPanel(int score, boolean marked, UserReference createdBy, boolean markable) {
        markButton = new MarkButton();
        voteIcon = new VoteIcon();
        helpedLabel = new HelpedLabel();

        this.createdBy = createdBy;
        // TODO the current user's userId is hacked here...
        this.currentUserId = IdeaSamebugPlugin.getInstance().getState().userId;

        setLayout(new FlowLayout(FlowLayout.LEFT, 5, 0));
        if (markable) add(markButton);
        add(new TransparentPanel() {
            {
                setPreferredSize(new Dimension(5, 0));
            }
        });
        add(voteIcon);
        add(helpedLabel);
        updateState(score, marked);
    }

    public void beginPostMark() {
        ApplicationManager.getApplication().assertIsDispatchThread();
        markButton.setEnabled(false);
    }

    public void finishPostMarkWithError(final String errorMessage) {
        ApplicationManager.getApplication().assertIsDispatchThread();
        markButton.setEnabled(true);
        JBPopupFactory.getInstance().createBalloonBuilder(new TransparentPanel() {
            {
                setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
                add(new JLabel() {
                    {
                        final HashMap<TextAttribute, Object> attributes = new HashMap<TextAttribute, Object>();
                        attributes.put(TextAttribute.SIZE, 12);
                        attributes.put(TextAttribute.WEIGHT, TextAttribute.WEIGHT_BOLD);
                        setFont(getFont().deriveFont(attributes));
                        setText(errorMessage);
                        setForeground(Colors.samebugWhite);
                    }
                });
            }
        }).setFillColor(Colors.alertPanel).createBalloon().show(RelativePoint.getCenterOf(markButton), Balloon.Position.above);
    }

    public void finishPostMarkWithSuccess(final int score, final boolean marked) {
        ApplicationManager.getApplication().assertIsDispatchThread();
        markButton.setEnabled(true);
        updateState(score, marked);
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

    void updateState(final int score, final boolean marked) {
        if (createdBy != null) {
            helpedLabel.setText(SamebugBundle.message("samebug.mark.markedBy.someone", createdBy.id == currentUserId ? "You" : createdBy.displayName, score));
        } else if (score == 0) {
            helpedLabel.setText(SamebugBundle.message("samebug.mark.markedBy.noone"));
        } else {
            helpedLabel.setText(SamebugBundle.message("samebug.mark.markedBy.anyone", score));
        }
        markButton.setHighlighted(marked);
        if (marked) {
            markButton.setToolTipText(SamebugBundle.message("samebug.mark.marked.tooltip"));
        } else {
            markButton.setToolTipText(SamebugBundle.message("samebug.mark.unmarked.tooltip"));
        }
    }
}
