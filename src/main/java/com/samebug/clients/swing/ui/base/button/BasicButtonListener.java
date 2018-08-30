/*
 * Copyright 2018 Samebug, Inc.
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
package com.samebug.clients.swing.ui.base.button;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

/**
 * Mimics functionality of javax.swing.plaf.basic.BasicButtonListener
 * <p>
 * If we need more, we can just lift it from there.
 * If it turns out that we need most of the functionality, maybe we can make BasicButton to extends AbstractButton.
 * Also, interaction listener (controlling the rollover color of the button) should be merged here.
 * <p>
 * We should also merge here the button onclick action, handling the case of disabled button here.
 */
public class BasicButtonListener implements MouseListener {
    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {
        BasicButton b = (BasicButton) e.getSource();
        if (!b.hasFocus() && b.isRequestFocusEnabled()) {
            b.requestFocus();
        }

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }
}
