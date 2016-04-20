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
import com.samebug.clients.idea.resources.SamebugBundle;
import com.samebug.clients.idea.resources.SamebugIcons;

import javax.swing.*;
import java.awt.*;

/**
 * Created by poroszd on 4/8/16.
 */
public class MarkPanel extends TransparentPanel {

    public final JButton markButton;
    public final JPanel voteIcon;
    public final JLabel helpedLabel;

    public MarkPanel(int score, boolean marked) {
        markButton = new MarkButton();
        voteIcon = new VoteIcon();
        helpedLabel = new HelpedLabel();

        setLayout(new FlowLayout(FlowLayout.LEFT, 10, 0));
        add(markButton);
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
                add(new JLabel(errorMessage));
            }
        }).setFillColor(Color.red).createBalloon().show(RelativePoint.getCenterOf(markButton), Balloon.Position.above);
    }

    public void finishPostMarkWithSuccess(final int score, final boolean marked) {
        ApplicationManager.getApplication().assertIsDispatchThread();
        markButton.setEnabled(true);
        updateState(score, marked);
        revalidate();
        repaint();
    }

    class MarkButton extends JButton {
        {
            setFocusable(false);
            setOpaque(false);
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
        if (score == 0) {
            helpedLabel.setText(SamebugBundle.message("samebug.mark.marked.noone"));
        } else {
            helpedLabel.setText(SamebugBundle.message("samebug.mark.marked.anyone", score));
        }
        if (marked) {
            markButton.setText("unmark");
        } else {
            markButton.setText("mark");
        }
    }
}
