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
package com.samebug.clients.swing.ui.component.util.errorBarPane;

import com.samebug.clients.swing.ui.ColorUtil;
import com.samebug.clients.swing.ui.SamebugIcons;
import com.samebug.clients.swing.ui.component.util.panel.SamebugPanel;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;

public class ErrorBar extends SamebugPanel {
    public ErrorBar(String text) {
        setBackground(ColorUtil.ErrorBar);

        // TODO shadow border
        setLayout(new MigLayout("", "20[]10[]20", "15[]15"));
        final JLabel alertIcon = new JLabel(SamebugIcons.alertErrorBar());
        final JLabel message = new JLabel(text);
        add(alertIcon, "cell 0 0");
        add(message, "cell 1 0");
    }
}
