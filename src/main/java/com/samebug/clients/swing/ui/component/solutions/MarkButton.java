/**
 * Copyright 2017 Samebug, Inc.
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
package com.samebug.clients.swing.ui.component.solutions;

import com.samebug.clients.common.ui.component.solutions.IMarkButton;
import com.samebug.clients.swing.ui.component.util.button.SamebugButton;
import com.samebug.clients.swing.ui.component.util.interaction.Colors;
import com.samebug.clients.swing.ui.component.util.label.SamebugLabel;
import com.samebug.clients.swing.ui.global.*;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public final class MarkButton extends JComponent implements IMarkButton {
    private Model model;
    private NormalMarkButton normalMarkButton;
    private LoadingButton loadingButton;

    private static final Colors[] ForegroundInteraction = ColorService.MarkInteraction;
    private static final Color[] Foreground = ColorService.Mark;
    private static final Color[] Background = ColorService.Background;


    public MarkButton(Model model) {
        setLayout(new MigLayout("fillx", "0[fill]0", "0[fill]0"));
        update(model);
    }

    public void setLoading() {
        removeAll();
        // NOTE do not null normalButton, because when loading fails, we have to revert to the previous state
        // TODO it would be cleaner to get the necessary model in interruptLoading as a parameter
        loadingButton = new LoadingButton();
        loadingButton.setPreferredSize(normalMarkButton.getPreferredSize());
        add(loadingButton);

        revalidate();
        repaint();
    }

    public void interruptLoading() {
        // TODO some better way to handle state change?
        removeAll();
        loadingButton = null;
        add(normalMarkButton);

        revalidate();
        repaint();
    }

    public void update(Model model) {
        this.model = new Model(model);

        removeAll();
        loadingButton = null;
        normalMarkButton = new NormalMarkButton();
        add(normalMarkButton);

        revalidate();
        repaint();
    }


    private final class NormalMarkButton extends SamebugButton {
        public NormalMarkButton() {
            final CounterLabel counter = new CounterLabel();
            final Separator separator = new Separator();
            final MarkLabel markLabel = new MarkLabel();

            setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
            setLayout(new MigLayout("", "12[]9[]10[]8", "8[]8"));

            add(counter, ", h 16!");
            add(separator, "w 1!, h 16!");
            add(markLabel, ", h 16!");

            setFont(FontService.demi(14));
            setForeground(ForegroundInteraction);
            setBackground(Background);

            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    getListener().markClicked(MarkButton.this, getSolutionId(), MarkButton.this.model.userMarkId);
                }
            });

            Color childrenForeground;
            if (MarkButton.this.model.userMarkId == null) {
                setFilled(false);
                childrenForeground = getForeground();
            } else {
                setFilled(true);
                childrenForeground = getBackground();
            }
            // Cheat. If the button is filled, we use the background color of the button as foreground color of the child components
            for (Component c : getComponents()) c.setForeground(childrenForeground);

            updateUI();
        }

        @Override
        protected void paintContent(Graphics2D g2) {
            // we paint the background on our own
            super.paintChildren(g2);
        }

        @Override
        public void setForeground(Color color) {
            super.setForeground(color);
            // when the button is not filled, we have to change the foreground of the children
            if (!isFilled()) {
                for (Component c : getComponents()) c.setForeground(color);
            }
        }

        private final class CounterLabel extends SamebugLabel {
            {
                setHorizontalAlignment(SwingConstants.CENTER);
                setFont(NormalMarkButton.this.getFont());

                setText(Integer.toString(MarkButton.this.model.marks));
            }
        }

        private final class MarkLabel extends SamebugLabel {
            {
                setHorizontalAlignment(SwingConstants.CENTER);
                setFont(NormalMarkButton.this.getFont());

                if (MarkButton.this.model.userMarkId == null) setText(MessageService.message("samebug.component.mark.mark"));
                else setText(MessageService.message("samebug.component.mark.marked"));
            }

            @Override
            public void paint(Graphics g) {
                super.paint(g);
            }
        }

        private final class Separator extends JComponent {
            @Override
            public void paint(Graphics g) {
                Graphics2D g2 = DrawService.init(g);
                g2.setColor(getForeground());

                g2.drawLine(0, 0, 0, 16);
            }
        }

    }

    private final class LoadingButton extends SamebugButton {
        public LoadingButton() {
            setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
            setLayout(new MigLayout("", ":push[]:push", "6[]6"));

            JLabel icon = new JLabel(IconService.loading());

            add(icon, "align center, h 20!");

            setForeground(ForegroundInteraction);
            setBackground(Background);
            updateUI();
        }

        @Override
        protected void paintContent(Graphics2D g2) {
            // we paint the background on our own
            super.paintChildren(g2);
        }
    }

    private Integer getSolutionId() {
        return DataService.getData(this, DataService.SolutionId);
    }

    private Listener getListener() {
        return ListenerService.getListener(this, IMarkButton.Listener.class);
    }
}
