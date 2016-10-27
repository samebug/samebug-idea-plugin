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

import com.intellij.openapi.application.ApplicationManager;
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
    final JComponent closedStateComponent;
    final JComponent openedStateComponent;

    public CollapsableView(final JComponent content, final JComponent closedStateComponent, final JComponent openedStateComponent) {
        ApplicationManager.getApplication().assertIsDispatchThread();
        this.content = content;
        this.closedStateComponent = closedStateComponent;
        this.openedStateComponent = openedStateComponent;
        this.collapsiblePane = new JXCollapsiblePane();
        this.openCloseBar = new OpenCloseBar();
        this.collapsiblePane.add(content);

        add(openCloseBar, BorderLayout.SOUTH);
        add(collapsiblePane, BorderLayout.CENTER);
        open();
    }

    public void open() {
        ApplicationManager.getApplication().assertIsDispatchThread();
        collapsiblePane.setCollapsed(false);
        openCloseBar.removeAll();
        openCloseBar.add(openedStateComponent);
    }

    public void close() {
        ApplicationManager.getApplication().assertIsDispatchThread();
        collapsiblePane.setCollapsed(true);
        openCloseBar.removeAll();
        openCloseBar.add(closedStateComponent);
    }

    final class OpenCloseBar extends TransparentPanel {
        OpenCloseBar() {
            add(openedStateComponent);

            addMouseListener(new MouseAdapter() {
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
