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
package com.samebug.clients.swing.ui.component.authentication;

import com.samebug.clients.common.ui.component.form.ErrorCodeMismatchException;
import com.samebug.clients.swing.ui.base.form.InputField;
import com.samebug.clients.swing.ui.base.label.SamebugLabel;
import com.samebug.clients.swing.ui.base.multiline.SamebugMultilineLabel;
import com.samebug.clients.swing.ui.base.panel.SamebugPanel;
import com.samebug.clients.swing.ui.modules.ColorService;
import com.samebug.clients.swing.ui.modules.FontService;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public abstract class FormField extends SamebugPanel {
    final SamebugLabel descriptionLabel;
    final InputField field;
    final ErrorLabel errorLabel;

    public FormField(String description) {
        descriptionLabel = new DescriptionLabel(description);
        field = new InputField();
        errorLabel = new ErrorLabel();

        field.addPropertyChangeListener(InputField.ERROR_PROPERTY, new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if (evt.getNewValue() instanceof Boolean && !((Boolean) evt.getNewValue())) {
                    remove(errorLabel);
                    revalidate();
                    repaint();
                }
            }
        });

        setLayout(new MigLayout("fillx", "0[0, fill]0", "0[]0[]0[]0"));
        add(descriptionLabel, "cell 0 0");
        add(field, "cell 0 1");
    }

    public String getText() {
        return field.getText();
    }

    public void setFormError(String errorCode) throws ErrorCodeMismatchException {
        field.setError(true);
        remove(errorLabel);
        updateErrorLabel(errorLabel, errorCode);
        add(errorLabel, "cell 0 2, wmin 0");
    }

    public void addActionListener(ActionListener actionListener) {
        field.addActionListener(actionListener);
    }

    protected abstract void updateErrorLabel(SamebugMultilineLabel errorLabel, String errorCode) throws ErrorCodeMismatchException;


    final class DescriptionLabel extends SamebugLabel {
        public DescriptionLabel(String text) {
            setText(text);
            setFont(FontService.regular(14));
            setForegroundColor(ColorService.NormalForm.fieldName);
            setBorder(BorderFactory.createEmptyBorder(6, 0, 6, 0));
        }
    }

    final class ErrorLabel extends SamebugMultilineLabel {
        public ErrorLabel() {
            setForegroundColor(ColorService.NormalForm.error);
            setFont(FontService.regular(12));
            setBorder(BorderFactory.createEmptyBorder(6, 0, 6, 0));
        }
    }
}
