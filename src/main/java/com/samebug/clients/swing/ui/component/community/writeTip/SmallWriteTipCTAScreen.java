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
package com.samebug.clients.swing.ui.component.community.writeTip;

import com.samebug.clients.swing.ui.base.button.SamebugButton;
import com.samebug.clients.swing.ui.base.multiline.SamebugMultilineLabel;
import com.samebug.clients.swing.ui.base.panel.EmphasizedPanel;
import com.samebug.clients.swing.ui.modules.MessageService;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;

final class SmallWriteTipCTAScreen extends EmphasizedPanel implements WriteTipCTAScreen {
    final SamebugButton button;
    final SamebugMultilineLabel label;

    SmallWriteTipCTAScreen(int usersWaitingHelp) {
        button = new SamebugButton();
        button.setText(MessageService.message("samebug.component.tip.write.cta.button"));
        label = new SamebugMultilineLabel();
        label.setText(MessageService.message("samebug.component.cta.writeTip.tips.label", usersWaitingHelp));

        setLayout(new MigLayout("fillx, w 300px", "20px[fill]50px[fill]10px", "20px[]20px"));
        add(button, "cell 0 0, al center center");
        add(label, "cell 1 0, wmin 0");
    }

    @Override
    public JComponent getCTAScreen() {
        return this;
    }

    @Override
    public JComponent getCTAButton() {
        return button;
    }
}
