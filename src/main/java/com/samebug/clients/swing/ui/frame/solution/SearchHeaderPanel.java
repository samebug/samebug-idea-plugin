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
package com.samebug.clients.swing.ui.frame.solution;

import com.samebug.clients.common.ui.frame.solution.ISearchHeaderPanel;
import com.samebug.clients.swing.ui.base.multiline.SamebugMultilineLabel;
import com.samebug.clients.swing.ui.base.panel.SamebugPanel;
import com.samebug.clients.swing.ui.modules.ColorService;
import com.samebug.clients.swing.ui.modules.FontService;
import com.samebug.clients.swing.ui.modules.ListenerService;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public final class SearchHeaderPanel extends SamebugPanel implements ISearchHeaderPanel {
    private final Model model;

    public SearchHeaderPanel(Model model) {
        this.model = new Model(model);

        final HeaderTextLabel title = new HeaderTextLabel();
        setLayout(new MigLayout("fillx, filly", "0[]0", "30[]30"));

        add(title, "wmin 0, hmax 56");
    }

    final class HeaderTextLabel extends SamebugMultilineLabel {
        {
            setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 20));
            setFont(FontService.demi(24));
            setForegroundColor(ColorService.EmphasizedText);

            setText(model.title);

            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    getListener().titleClicked();
                }
            });
        }

        @Override
        public Dimension getPreferredSize() {
            // TODO this breaks when changing font
            if (getLineCount() <= 1) {
                return new Dimension(Integer.MAX_VALUE, 24 + 2);
            } else {
                return new Dimension(Integer.MAX_VALUE, 24 * 2 + 8);
            }
        }
    }

    private Listener getListener() {
        return ListenerService.getListener(this, ISearchHeaderPanel.Listener.class);
    }
}

