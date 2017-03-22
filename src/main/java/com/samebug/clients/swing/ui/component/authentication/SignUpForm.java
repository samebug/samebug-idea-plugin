package com.samebug.clients.swing.ui.component.authentication;

import com.samebug.clients.common.api.form.FieldError;
import com.samebug.clients.common.ui.component.authentication.ISignUpForm;
import com.samebug.clients.common.ui.component.form.ErrorCodeMismatchException;
import com.samebug.clients.common.ui.component.form.FormMismatchException;
import com.samebug.clients.swing.ui.base.button.SamebugButton;
import com.samebug.clients.swing.ui.base.multiline.SamebugMultilineLabel;
import com.samebug.clients.swing.ui.modules.ListenerService;
import com.samebug.clients.swing.ui.modules.MessageService;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public final class SignUpForm extends JComponent implements ISignUpForm {
    final FormField displayName;
    final FormField email;
    final PasswordFormField password;
    final SamebugButton signUp;

    {
        displayName = new DisplayNameField();
        email = new EmailField();
        password = new PasswordField();
        signUp = new SignUpButton();

        signUp.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (isEnabled()) getListener().signUp(SignUpForm.this, displayName.getText(), email.getText(), password.getText());
            }
        });

        setLayout(new MigLayout("fillx", "0[120!]20[120!]0", "0[]10[]10[]20[]0"));
        add(displayName, "cell 0 0, spanx 2, growx");
        add(email, "cell 0 1, spanx 2, growx");
        add(password, "cell 0 2, spanx 2, growx");
        add(signUp, "cell 0 3, growx");
    }

    @Override
    public void startPost() {

    }

    @Override
    public void failPost(List<FieldError> errors) throws FormMismatchException {

    }

    @Override
    public void successPost() {

    }


    final class DisplayNameField extends FormField {
        public DisplayNameField() {
            super(MessageService.message("samebug.component.authentication.displayName"));
        }

        @Override
        protected void updateErrorLabel(SamebugMultilineLabel errorLabel, String errorCode) throws ErrorCodeMismatchException {

        }
    }

    final class EmailField extends FormField {
        public EmailField() {
            super(MessageService.message("samebug.component.authentication.email"));
        }

        @Override
        protected void updateErrorLabel(SamebugMultilineLabel errorLabel, String errorCode) throws ErrorCodeMismatchException {

        }
    }

    final class PasswordField extends PasswordFormField {
        public PasswordField() {
            super(MessageService.message("samebug.component.authentication.password"));
        }

        @Override
        protected void updateErrorLabel(SamebugMultilineLabel errorLabel, String errorCode) throws ErrorCodeMismatchException {

        }
    }

    final class SignUpButton extends SamebugButton {
        {
            setFilled(true);
            setText(MessageService.message("samebug.component.authentication.signUp"));
        }
    }

    Listener getListener() {
        return ListenerService.getListener(this, Listener.class);
    }
}
