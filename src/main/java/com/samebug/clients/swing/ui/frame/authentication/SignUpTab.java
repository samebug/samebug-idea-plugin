package com.samebug.clients.swing.ui.frame.authentication;

import com.samebug.clients.common.ui.component.form.ErrorCodeMismatchException;
import com.samebug.clients.swing.ui.base.button.SamebugButton;
import com.samebug.clients.swing.ui.base.label.LinkLabel;
import com.samebug.clients.swing.ui.base.multiline.SamebugMultilineLabel;
import com.samebug.clients.swing.ui.component.authentication.Delimeter;
import com.samebug.clients.swing.ui.component.authentication.FormField;
import com.samebug.clients.swing.ui.component.authentication.PasswordFormField;
import com.samebug.clients.swing.ui.modules.MessageService;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;

public final class SignUpTab extends JComponent {
    final FormField displayName;
    final FormField email;
    final PasswordFormField password;
    final SamebugButton signUp;
    final SamebugButton useAnonymously;

    {
        displayName = new DisplayNameField();
        email = new EmailField();
        password = new PasswordField();
        signUp = new SignUpButton();
        final JComponent delimeter = new Delimeter();
        useAnonymously = new UseAnonButton();

        setLayout(new MigLayout("fillx", "0[120!]20[120!]0", "0[]10[]10[]20[]10[]10[]40"));
        add(displayName, "cell 0 0, spanx 2, growx");
        add(email, "cell 0 1, spanx 2, growx");
        add(password, "cell 0 2, spanx 2, growx");
        add(signUp, "cell 0 3, growx");
        add(delimeter, "cell 0 4, spanx 2, growx");
        add(useAnonymously, "cell 0 5, spanx 2, growx");
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

    final class UseAnonButton extends SamebugButton {
        {
            setFilled(false);
            setText(MessageService.message("samebug.component.authentication.anonymousUse"));
        }
    }
}
