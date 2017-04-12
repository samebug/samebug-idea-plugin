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

import com.samebug.clients.swing.ui.base.form.InputField;
import com.samebug.clients.swing.ui.base.form.PasswordInputField;
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

// TODO extract common parts with FormField
public abstract class PasswordFormField<T> extends SamebugPanel {
    final SamebugLabel descriptionLabel;
    final PasswordInputField field;
    final ErrorLabel errorLabel;

    public PasswordFormField(String description) {
        descriptionLabel = new DescriptionLabel(description);
        field = new PasswordInputField();
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
        // TODO handle password sensitively
        return new String(field.getPassword());
    }

    public void setFormError(T errorCode) {
        field.setError(true);
        remove(errorLabel);
        updateErrorLabel(errorLabel, errorCode);
        add(errorLabel, "cell 0 2, wmin 0");
    }

    protected abstract void updateErrorLabel(SamebugMultilineLabel errorLabel, T errorCode);

    public void addActionListener(ActionListener actionListener) {
        field.addActionListener(actionListener);
    }

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
