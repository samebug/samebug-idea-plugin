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
import org.jdesktop.swingx.JXCollapsiblePane;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

final public class CollapsableView extends TransparentPanel {
    final JComponent content;
    final JXCollapsiblePane collapsiblePane;
    final OpenCloseBar openCloseBar;
    final String closedStateText;
    final String openedStateText;

    public CollapsableView(final JComponent content, final String closedStateText, final String openedStateText) {
        this.content = content;
        this.closedStateText = closedStateText;
        this.openedStateText = openedStateText;
        this.collapsiblePane = new JXCollapsiblePane();
        this.openCloseBar = new OpenCloseBar();
        this.collapsiblePane.add(content);

        add(openCloseBar, BorderLayout.SOUTH);
        add(collapsiblePane, BorderLayout.CENTER);
        open();
    }

    public void open() {
        collapsiblePane.setCollapsed(false);
        openCloseBar.label.setText(openedStateText);
    }

    public void close() {
        collapsiblePane.setCollapsed(true);
        openCloseBar.label.setText(closedStateText);
    }

    final class OpenCloseBar extends TransparentPanel {
        JLabel label;

        public OpenCloseBar() {
            this.label = new JLabel(openedStateText) {
                {
                    setHorizontalAlignment(SwingConstants.CENTER);
                }
            };
            add(label);

            label.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (collapsiblePane.isCollapsed()) {
                        open();
                    } else {
                        close();
                    }
                }
            });
        }
    }
}
