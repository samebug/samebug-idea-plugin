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
import com.samebug.clients.swing.ui.base.form.InputField;
import com.samebug.clients.swing.ui.base.multiline.SamebugMultilineLabel;
import com.samebug.clients.swing.ui.modules.ColorService;
import com.samebug.clients.swing.ui.modules.FontService;
import net.miginfocom.swing.MigLayout;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public final class SourceUrlField extends JComponent {
    final InputField field;
    final ErrorLabel errorLabel;

    public SourceUrlField(@NotNull String placeholder) {
        field = new InputField(placeholder);
        field.setColors(ColorService.TipForm);
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

        setLayout(new MigLayout("fillx", "0[0, fill]0", "0[]0[]0"));
        add(field, "cell 0 0");
    }

    public String getText() {
        return field.getText();
    }

    public void setFormError(@NotNull IHelpOthersCTA.BadRequest.SourceUrl error) {
        field.setError(true);
        switch (error) {
            case UNREACHABLE:
                errorLabel.setText(MessageService.message("samebug.component.tip.write.error.sourceUrl.unreachable"));
                break;
            case UNRECOGNIZED:
                errorLabel.setText(MessageService.message("samebug.component.tip.write.error.sourceUrl.unrecognized"));
                break;
            default:
        }
        remove(errorLabel);
        add(errorLabel, "cell 0 1, wmin 0");
    }

    final class ErrorLabel extends SamebugMultilineLabel {
        ErrorLabel() {
            setForegroundColor(ColorService.TipForm.error);
            setFont(FontService.regular(12));
            setBorder(BorderFactory.createEmptyBorder(6, 0, 6, 0));
        }
    }
}
