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
package com.samebug.clients.idea.ui.components;

import com.intellij.ide.BrowserUtil;
import com.intellij.util.ui.UIUtil;
import com.samebug.clients.idea.resources.SamebugIcons;
import com.samebug.clients.search.api.entities.ComponentStack;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Created by poroszd on 3/17/16.
 */
public class BreadcrumbBar extends JPanel {
    public BreadcrumbBar(java.util.List<ComponentStack> stacks) {
        assert (stacks.size() > 0);
        setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
        setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        add(new BreadcrumbEndLabel());
        add(new BreadcrumbLabel(stacks.get(0)));
        for (int i = 1; i < stacks.size(); ++i) {
            add(new BreadcrumbDelimeterLabel());
            add(new BreadcrumbLabel(stacks.get(i)));
        }

    }

    public class BreadcrumbEndLabel extends JLabel {
        @Override
        public Icon getIcon() {
            return SamebugIcons.breadcrumbEnd;
        }
    }

    public class BreadcrumbDelimeterLabel extends JLabel {
        @Override
        public Icon getIcon() {
            return SamebugIcons.breadcrumbDelimeter;
        }
    }

    public class BreadcrumbLabel extends JLabel {
        ComponentStack stack;

        public BreadcrumbLabel(final ComponentStack stack) {
            this.stack = stack;
            if (stack.crashDocUrl != null) {
                setCursor(new Cursor(Cursor.HAND_CURSOR));
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
            else return color(stack.color);
        }

    }

    static private Color color(int componentColorCode) {
        if (UIUtil.isUnderDarcula()) {
            return DARCULA_COLORS[componentColorCode];
        } else {
            return DEFAULT_COLORS[componentColorCode];
        }
    }


    static private final Color[] DEFAULT_COLORS = {new Color(0x9A9A9A), new Color(0x14E3CF), new Color(0x8BC349), new Color(0x00384F),
            new Color(0x9C27B0), new Color(0xFF00EB), new Color(0x03B8D4), new Color(0x79141D),
            new Color(0xFFB600), new Color(0x3000E7), new Color(0x3EABFF), new Color(0xD50000),
            new Color(0x443328), new Color(0xE91D63), new Color(0x029688), new Color(0xB0BF16), new Color(0xFF5621)};

    static private final Color[] DARCULA_COLORS = {new Color(0x9A9A9A), new Color(0x14E3CF), new Color(0x8BC349), new Color(0x0080B5),
            new Color(0xBC37D3), new Color(0xFF00EB), new Color(0x03B8D4), new Color(0xD12232),
            new Color(0xFFB600), new Color(0x9B81FF), new Color(0x3EABFF), new Color(0xD50000),
            new Color(0xA37C62), new Color(0xE91D63), new Color(0x029688), new Color(0xB0BF16), new Color(0xFF5621)};

}
