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
            setBorder(BorderFactory.createEmptyBorder(6, 0, 6, 0));
        }
    }
}
