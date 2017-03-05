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
package com.samebug.clients.swing.ui.component.solutions;

import com.samebug.clients.common.ui.component.solutions.IHelpOthersCTA;
import com.samebug.clients.swing.ui.component.util.button.SamebugButton;
import com.samebug.clients.swing.ui.component.util.multiline.CenteredMultilineLabel;
import com.samebug.clients.swing.ui.component.util.panel.EmphasizedPanel;
import com.samebug.clients.swing.ui.global.ListenerService;
import com.samebug.clients.swing.ui.global.MessageService;
import net.miginfocom.swing.MigLayout;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class LargeWriteTipCTA extends EmphasizedPanel implements IHelpOthersCTA {
    protected final Model model;

    protected final CenteredMultilineLabel label;

    public LargeWriteTipCTA(Model model) {
        this.model = new Model(model);

        final SamebugButton button = new SamebugButton(MessageService.message("samebug.component.tip.write.cta.button"), true);
        label = new CenteredMultilineLabel();

        setLayout(new MigLayout("fillx, w 300", "40[]40", "40[]20[]40"));
        add(label, "cell 0 0, wmin 0, growx");
        add(button, "cell 0 1, align center");

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                getListener().ctaClicked(LargeWriteTipCTA.this);
            }
        });
    }

    private Listener getListener() {
        return ListenerService.getListener(this, IHelpOthersCTA.Listener.class);
    }
}
