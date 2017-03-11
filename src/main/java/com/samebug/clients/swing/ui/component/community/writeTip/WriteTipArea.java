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

import com.samebug.clients.swing.ui.base.form.LengthRestrictedArea;
import com.samebug.clients.swing.ui.modules.ColorService;
import com.samebug.clients.swing.ui.modules.MessageService;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;

public final class WriteTipArea extends JComponent {
    static final int MaxCharacters = 140;

    final int peopleToHelp;
    final BorderedArea borderedArea;

    public WriteTipArea(int peopleToHelp) {
        this.peopleToHelp = peopleToHelp;
        borderedArea = new BorderedArea();

        setLayout(new MigLayout("fillx", "0[fill]0", "0[]0"));
        add(borderedArea);
    }

    final class BorderedArea extends LengthRestrictedArea {
        public BorderedArea() {
            super(ColorService.TipForm);
            setLayout(new MigLayout("fillx", "10[300]10", "10[]10[]6"));
            add(myEditableArea, "cell 0 0, wmin 0, growx");
            add(myLengthCounter, "cell 0 1, align right");
        }

        @Override
        protected EditableArea createEditableArea() {
            return new EditableArea(MessageService.message("samebug.component.tip.write.placeholder", peopleToHelp));
        }

        @Override
        protected LengthCounter createLengthCounter() {
            return new LengthCounter(MaxCharacters);
        }
    }
}

