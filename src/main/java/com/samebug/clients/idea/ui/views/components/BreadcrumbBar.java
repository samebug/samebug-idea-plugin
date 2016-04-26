/**
 * Copyright 2016 Samebug, Inc.
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.samebug.clients.idea.ui.views.components;

import com.samebug.clients.idea.resources.SamebugIcons;
import com.samebug.clients.idea.ui.BrowserUtil;
import com.samebug.clients.idea.ui.ColorUtil;
import com.samebug.clients.search.api.entities.ComponentStack;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class BreadcrumbBar extends TransparentPanel {
    public BreadcrumbBar(@NotNull java.util.List<ComponentStack> stacks) {
        setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
        setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        add(new BreadcrumbEndLabel());
        if (!stacks.isEmpty()) add(new BreadcrumbLabel(stacks.get(0)));
        for (int i = 1; i < stacks.size(); ++i) {
            add(new BreadcrumbDelimeterLabel());
            add(new BreadcrumbLabel(stacks.get(i)));
        }

    }

    class BreadcrumbEndLabel extends JLabel {
        @Override
        public Icon getIcon() {
            return SamebugIcons.breadcrumbEnd;
        }
    }

    class BreadcrumbDelimeterLabel extends JLabel {
        @Override
        public Icon getIcon() {
            return SamebugIcons.breadcrumbDelimeter;
        }
    }

    class BreadcrumbLabel extends JLabel {
        ComponentStack stack;

        public BreadcrumbLabel(@NotNull final ComponentStack stack) {
            this.stack = stack;
            if (stack.crashDocUrl != null) {
                setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        BrowserUtil.browse(stack.crashDocUrl);
                    }
                });
            }
        }

        @Override
        public String getText() {
            if (stack == null) return super.getText();
            else return stack.shortName;
        }

        @Override
        public Color getForeground() {
            if (stack == null) return super.getForeground();
            else return ColorUtil.componentColors(stack.color);
        }
    }
}
