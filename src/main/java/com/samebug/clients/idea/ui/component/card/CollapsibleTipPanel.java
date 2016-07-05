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
package com.samebug.clients.idea.ui.component.card;

import com.samebug.clients.idea.ui.component.TransparentPanel;
import com.samebug.clients.idea.ui.component.WriteTip;
import com.samebug.clients.idea.ui.component.WriteTipHint;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

final public class CollapsibleTipPanel extends TransparentPanel {
    @Nullable
    WriteTip openedView;
    @Nullable
    WriteTipHint closedView;
    @NotNull
    final Actions actions;

    public CollapsibleTipPanel(@NotNull final Actions actions) {
        this.actions = actions;
        // TODO repaint somewhere else
        close();
    }

    public void beginPostTip() {
        if (openedView != null) openedView.beginPostTip();
    }

    public void finishPostTipWithError(final String message) {
        if (openedView != null) openedView.finishPostTipWithError(message);
    }

    public void finishPostTipWithSuccess() {
        if (openedView != null) openedView.finishPostTipWithSuccess();
    }

    void open() {
        if (closedView != null) remove(closedView);
        closedView = null;
        openedView = new WriteTip();
        add(openedView);
        openedView.cancel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                CollapsibleTipPanel.this.close();
            }
        });
        openedView.cancel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                actions.onCancel();
            }
        });
        openedView.submit.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                actions.onSubmit(openedView.tipBody.getText(), openedView.sourceLink.getText());
            }
        });
        revalidate();
        repaint();
    }

    void close() {
        if (openedView != null) remove(openedView);
        openedView = null;
        closedView = new WriteTipHint();
        add(closedView);
        closedView.ctaButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                CollapsibleTipPanel.this.open();
            }
        });
        closedView.ctaButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                actions.onCTA();
            }
        });
        revalidate();
        repaint();
    }

    public interface Actions {
        void onCTA();
        void onCancel();
        void onSubmit(String tip, String rawSourceUrl);
    }
}
