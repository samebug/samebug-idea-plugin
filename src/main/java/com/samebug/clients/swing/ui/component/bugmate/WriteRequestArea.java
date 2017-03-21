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

import com.samebug.clients.common.api.form.CreateHelpRequest;
import com.samebug.clients.common.ui.component.form.ErrorCodeMismatchException;
import com.samebug.clients.common.ui.component.form.IFormField;
import com.samebug.clients.swing.ui.base.form.LengthRestrictedArea;
import com.samebug.clients.swing.ui.base.form.MaxCharactersConstraints;
import com.samebug.clients.swing.ui.base.multiline.SamebugMultilineLabel;
import com.samebug.clients.swing.ui.component.helpRequest.ExceptionPreview;
import com.samebug.clients.swing.ui.modules.ColorService;
import com.samebug.clients.swing.ui.modules.MessageService;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import javax.swing.text.AbstractDocument;

public final class WriteRequestArea extends JComponent implements IFormField {
    public static final int MaxOvershootCharacters = 200;
    public static final int MaxCharacters = 140;

    final RequestHelp requestHelp;
    final BorderedArea borderedArea;
    final ErrorLabel errorLabel;

    public WriteRequestArea(RequestHelp requestHelp) {
        this.requestHelp = requestHelp;
        borderedArea = new BorderedArea();
        errorLabel = new ErrorLabel();

        setLayout(new MigLayout("fillx", "0[300, fill]0", "0[]0"));
        add(borderedArea);
    }

    @Override
    public String getText() {
        return borderedArea.getText();
    }

    @Override
    public void setFormError(String errorCode) throws ErrorCodeMismatchException {
        borderedArea.setError();
        if (CreateHelpRequest.E_TOO_LONG.equals(errorCode)) errorLabel.setText(MessageService.message("samebug.component.helpRequest.ask.error.long"));
        else throw new ErrorCodeMismatchException(errorCode);
        remove(errorLabel);
        add(errorLabel, "cell 0 1, wmin 0");
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
            return new EditableArea(MessageService.message("samebug.component.helpRequest.ask.placeholder"));
        }

        @Override
        protected LengthCounter createLengthCounter() {
            return new LengthCounter(MaxCharacters);
        }

        private final class MyExceptionPreview extends ExceptionPreview {
            public MyExceptionPreview() {
                super(requestHelp.model.exceptionTitle);
                setBackgroundColor(ColorService.Tip);
                setForegroundColor(ColorService.TipText);
            }
        }
    }

    final class ErrorLabel extends SamebugMultilineLabel {
        public ErrorLabel() {
            setForegroundColor(ColorService.NormalForm.error);
            setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
        }
    }
}
