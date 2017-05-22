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

import com.samebug.clients.common.ui.component.community.IHelpOthersCTA;
import com.samebug.clients.common.ui.modules.MessageService;
import com.samebug.clients.swing.ui.base.form.LengthRestrictedArea;
import com.samebug.clients.swing.ui.base.multiline.SamebugMultilineLabel;
import com.samebug.clients.swing.ui.modules.ColorService;
import com.samebug.clients.swing.ui.modules.FontService;
import net.miginfocom.swing.MigLayout;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public final class WriteTipArea extends JComponent {
    static final int MaxCharacters = 256;

    final String placeholder;
    final BorderedArea borderedArea;
    final ErrorLabel errorLabel;

    public WriteTipArea(@NotNull String placeholder) {
        this.placeholder = placeholder;
        borderedArea = new BorderedArea();
        errorLabel = new ErrorLabel();

        borderedArea.addPropertyChangeListener(LengthRestrictedArea.ERROR_PROPERTY, new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if (evt.getNewValue() instanceof Boolean && !((Boolean) evt.getNewValue())) {
                    remove(errorLabel);
                    revalidate();
                    repaint();
                }
            }
        });

        setLayout(new MigLayout("fillx", "0[300px, fill]0", "0[]0[]0"));
        add(borderedArea, "cell 0 0");
    }

    public String getText() {
        return borderedArea.getText();
    }

    public void setFormError(@NotNull IHelpOthersCTA.BadRequest.TipBody error) {
        borderedArea.setError(true);
        switch (error) {
            case TOO_SHORT:
                errorLabel.setText(MessageService.message("samebug.component.tip.write.error.tip.short"));
                break;
            case TOO_LONG:
                errorLabel.setText(MessageService.message("samebug.component.tip.write.error.tip.long"));
                break;
            default:
        }
        remove(errorLabel);
        add(errorLabel, "cell 0 1, wmin 0");
    }

    final class BorderedArea extends LengthRestrictedArea {
        public BorderedArea() {
            super(ColorService.TipForm);
            setLayout(new MigLayout("fillx", "10px[300px]10px", "10px[]10px[]6px"));
            add(myEditableArea, "cell 0 0, wmin 0, growx");
            add(myLengthCounter, "cell 0 1, align right");
        }

        @Override
        protected EditableArea createEditableArea() {
            return new EditableArea(placeholder);
        }

        @Override
        protected LengthCounter createLengthCounter() {
            return new LengthCounter(MaxCharacters);
        }

        @Override
        public void requestFocus() {
            myEditableArea.requestFocus();
        }
    }

    final class ErrorLabel extends SamebugMultilineLabel {
        public ErrorLabel() {
            setForegroundColor(ColorService.TipForm.error);
            setFont(FontService.regular(12));
            setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
        }
    }
}

