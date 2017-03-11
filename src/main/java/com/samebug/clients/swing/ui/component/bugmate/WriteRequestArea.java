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
package com.samebug.clients.swing.ui.component.bugmate;

import com.samebug.clients.swing.ui.base.form.LengthRestrictedArea;
import com.samebug.clients.swing.ui.base.form.MaxCharactersConstraints;
import com.samebug.clients.swing.ui.component.tipRequest.ExceptionPreview;
import com.samebug.clients.swing.ui.modules.ColorService;
import com.samebug.clients.swing.ui.modules.MessageService;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import javax.swing.text.AbstractDocument;

public final class WriteRequestArea extends JComponent {
    public static final int MaxOvershootCharacters = 200;
    public static final int MaxCharacters = 140;

    final BorderedArea borderedArea;
    final BugmateList bugmateList;

    public WriteRequestArea(BugmateList bugmateList) {
        this.bugmateList = bugmateList;
        borderedArea = new BorderedArea();

        setLayout(new MigLayout("fillx", "0[fill]0", "0[]0"));
        add(borderedArea);
    }

    final class BorderedArea extends LengthRestrictedArea {
        final JComponent exceptionPreview;

        public BorderedArea() {
            super(ColorService.NormalForm);
            exceptionPreview = new MyExceptionPreview();

            setLayout(new MigLayout("fillx", "10[300]10", "10[]10[]6[]10"));
            add(myEditableArea, "cell 0 0, wmin 0, growx");
            add(myLengthCounter, "cell 0 1, align right");
            add(exceptionPreview, "cell 0 2, wmin 0, growx");

            setBackgroundColor(ColorService.Background);
            ((AbstractDocument) myEditableArea.getDocument()).setDocumentFilter(new MaxCharactersConstraints(MaxOvershootCharacters));
        }

        @Override
        protected EditableArea createEditableArea() {
            return new EditableArea(MessageService.message("samebug.component.bugmate.ask.placeholder"));
        }

        @Override
        protected LengthCounter createLengthCounter() {
            return new LengthCounter(MaxCharacters);
        }

        private final class MyExceptionPreview extends ExceptionPreview {
            public MyExceptionPreview() {
                super(bugmateList.model.exceptionTitle);
                setBackgroundColor(ColorService.Tip);
                setForegroundColor(ColorService.TipText);
            }
        }
    }
}
