/*
 * Copyright 2017 Samebug, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 *    http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.samebug.clients.swing.ui.component.hit;

import com.samebug.clients.common.ui.component.hit.IMarkButton;
import com.samebug.clients.idea.tracking.Events;
import com.samebug.clients.swing.ui.base.button.ActionButton;
import com.samebug.clients.swing.ui.base.label.SamebugLabel;
import com.samebug.clients.swing.ui.modules.*;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public final class MarkButton extends ActionButton implements IMarkButton {
    private Model model;

    public MarkButton(Model model) {
        this.model = new Model(model);
        normalState();
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (isEnabled()) {
                    getListener().markClicked(MarkButton.this, getSolutionId(), MarkButton.this.model.userMarkId);

                    if (MarkButton.this.model.userMarkId == null) {
                        TrackingService.trace(Events.mark());
                    } else {
                        TrackingService.trace(Events.markCancelled());
                    }
                }
            }
        });
    }

    @Override
    public void revertFromLoadingAnimation() {
        super.revertFromLoadingAnimation();
        normalState();
        revalidate();
        repaint();
    }

    public void setLoading() {
        changeToLoadingAnimation();
    }

    public void interruptLoading() {
        revertFromLoadingAnimation();
    }

    public Model getModel() {
        return model;
    }

    public void update(Model model) {
        this.model = new Model(model);
        revertFromLoadingAnimation();
    }

    private void normalState() {
        final boolean markedByMe = model.userMarkId != null;
        final CounterLabel counter = new CounterLabel();
        final Separator separator = new Separator();
        final MarkLabel markLabel = new MarkLabel(markedByMe, model.userCanMark);

        setLayout(new MigLayout("", "12px[]9px[]10px[]8px", "8px[]8px"));

        add(counter, ", h 16!");
        add(separator, "w 1!, h 16!");
        add(markLabel, ", h 16!");

        setFilled(markedByMe);
        setEnabled(model.userCanMark);
        setFont(FontService.demi(14));
        setInteractionColors(ColorService.MarkInteraction);
        setBackgroundColor(backgroundColor);
    }

    private final class CounterLabel extends SamebugLabel {
        {
            setHorizontalAlignment(SwingConstants.CENTER);
            setText(Integer.toString(model.marks));
        }
    }

    private final class MarkLabel extends SamebugLabel {
        public MarkLabel(boolean markedByMe, boolean markableByMe) {
            setHorizontalAlignment(SwingConstants.CENTER);
            if (markableByMe && !markedByMe) setText(MessageService.message("samebug.component.mark.mark"));
            else if (markableByMe && markedByMe) setText(MessageService.message("samebug.component.mark.marked"));
            else setText(MessageService.message("samebug.component.mark.marked")); // we have the same text when the button is disabled
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

    private Integer getSolutionId() {
        return DataService.getData(this, DataService.SolutionId);
    }

    private Listener getListener() {
        return ListenerService.getListener(this, IMarkButton.Listener.class);
    }
}
