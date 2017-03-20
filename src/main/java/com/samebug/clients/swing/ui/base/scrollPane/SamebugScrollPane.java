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
package com.samebug.clients.swing.ui.base.scrollPane;

import com.samebug.clients.swing.ui.modules.ColorService;

import javax.swing.*;
import javax.swing.plaf.basic.BasicScrollPaneUI;
import java.awt.*;

public class SamebugScrollPane extends JScrollPane {
    private final Color[] Background = ColorService.Background;

    public SamebugScrollPane() {
        this(null);
    }

    public SamebugScrollPane(JComponent view) {
        super(view);
        setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        setBackground(ColorService.forCurrentTheme(Background));
    }

    @Override
    public JScrollBar createVerticalScrollBar() {
        return new SamebugScrollBar(Adjustable.VERTICAL);
    }

    @Override
    public JScrollBar createHorizontalScrollBar() {
        return new SamebugScrollBar(Adjustable.HORIZONTAL);
    }

    @Override
    public void updateUI() {
        setUI(new BasicScrollPaneUI());
        setBackground(ColorService.forCurrentTheme(Background));
    }
}
