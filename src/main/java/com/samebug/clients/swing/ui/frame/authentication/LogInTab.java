package com.samebug.clients.swing.ui.frame.authentication;

import com.samebug.clients.common.ui.component.form.ErrorCodeMismatchException;
import com.samebug.clients.swing.ui.base.button.SamebugButton;
import com.samebug.clients.swing.ui.base.label.LinkLabel;
import com.samebug.clients.swing.ui.base.multiline.SamebugMultilineLabel;
import com.samebug.clients.swing.ui.component.authentication.Delimeter;
import com.samebug.clients.swing.ui.component.authentication.FormField;
import com.samebug.clients.swing.ui.component.authentication.PasswordFormField;
import com.samebug.clients.swing.ui.modules.FontService;
import com.samebug.clients.swing.ui.modules.MessageService;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;

public final class LogInTab extends JComponent {
    final FormField email;
    final PasswordFormField password;
    final SamebugButton logIn;
    final LinkLabel forgotPassword;
    final SamebugButton useAnonymously;

    {
        email = new EmailField();
        password = new PasswordField();
        logIn = new LogInButton();
        forgotPassword = new ForgotPasswordLabel();
        final JComponent delimeter = new Delimeter();
        useAnonymously = new UseAnonButton();

        setLayout(new MigLayout("fillx", "0[120!]20[120!]0", "0[]10[]20[]10[]10[]40"));
        add(email, "cell 0 0, spanx 2, growx");
        add(password, "cell 0 1, spanx 2, growx");
        add(logIn, "cell 0 2, growx");
        add(forgotPassword, "cell 1 2, al right");
        add(delimeter, "cell 0 3, spanx 2, growx");
        add(useAnonymously, "cell 0 4, spanx 2, growx");
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

    final class LogInButton extends SamebugButton {
        {
            setFilled(true);
            setText(MessageService.message("samebug.component.authentication.logIn"));
        }
    }

    final class ForgotPasswordLabel extends LinkLabel {
        {
            setText(MessageService.message("samebug.component.authentication.forgotPassword"));
            setFont(FontService.demi(14));
        }
    }

    final class UseAnonButton extends SamebugButton {
        {
            setFilled(false);
            setText(MessageService.message("samebug.component.authentication.anonymousUse"));
        }
    }

}
