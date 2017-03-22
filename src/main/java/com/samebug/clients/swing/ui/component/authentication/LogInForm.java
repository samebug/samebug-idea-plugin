package com.samebug.clients.swing.ui.component.authentication;

import com.samebug.clients.common.api.form.FieldError;
import com.samebug.clients.common.ui.component.authentication.ILogInForm;
import com.samebug.clients.common.ui.component.form.ErrorCodeMismatchException;
import com.samebug.clients.common.ui.component.form.FormMismatchException;
import com.samebug.clients.swing.ui.base.button.SamebugButton;
import com.samebug.clients.swing.ui.base.label.LinkLabel;
import com.samebug.clients.swing.ui.base.multiline.SamebugMultilineLabel;
import com.samebug.clients.swing.ui.modules.FontService;
import com.samebug.clients.swing.ui.modules.ListenerService;
import com.samebug.clients.swing.ui.modules.MessageService;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public final class LogInForm extends JComponent implements ILogInForm {
    final FormField email;
    final PasswordFormField password;
    final SamebugButton logIn;
    final LinkLabel forgotPassword;

    {
        email = new EmailField();
        password = new PasswordField();
        logIn = new LogInButton();
        forgotPassword = new ForgotPasswordLabel();

        logIn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (isEnabled()) getListener().logIn(LogInForm.this, email.getText(), password.getText());
            }
        });
        forgotPassword.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (isEnabled()) getListener().forgotPassword(LogInForm.this);
            }
        });

        setLayout(new MigLayout("fillx", "0[120!]20[120!]0", "0[]10[]20[]0"));
        add(email, "cell 0 0, spanx 2, growx");
        add(password, "cell 0 1, spanx 2, growx");
        add(logIn, "cell 0 2, growx");
        add(forgotPassword, "cell 1 2, al right");
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

    Listener getListener() {
        return ListenerService.getListener(this, Listener.class);
    }

}
