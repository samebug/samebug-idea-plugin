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

import com.samebug.clients.common.api.form.FieldError;
import com.samebug.clients.common.api.form.LogIn;
import com.samebug.clients.common.ui.component.authentication.ILogInForm;
import com.samebug.clients.common.ui.component.form.ErrorCodeMismatchException;
import com.samebug.clients.common.ui.component.form.FieldNameMismatchException;
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
import java.util.ArrayList;
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
        logIn.changeToLoadingAnimation();
    }

    @Override
    public void failPost(List<FieldError> errors) throws FormMismatchException {
        logIn.revertFromLoadingAnimation();

        List<FieldError> mismatched = new ArrayList<FieldError>();
        for (FieldError f : errors) {
            try {
                if (f.key.equals(LogIn.EMAIL)) email.setFormError(f.code);
                else if (f.key.equals(LogIn.PASSWORD)) password.setFormError(f.code);
                else throw new FieldNameMismatchException(f.key);
            } catch (ErrorCodeMismatchException e) {
                mismatched.add(f);
            } catch (FieldNameMismatchException e) {
                mismatched.add(f);
            }
        }
        if (!mismatched.isEmpty()) throw new FormMismatchException(mismatched);
    }

    @Override
    public void successPost() {
        logIn.revertFromLoadingAnimation();
    }


    final class EmailField extends FormField {
        public EmailField() {
            super(MessageService.message("samebug.component.authentication.email"));
        }

        @Override
        protected void updateErrorLabel(SamebugMultilineLabel errorLabel, String errorCode) throws ErrorCodeMismatchException {
            if (errorCode.equals(LogIn.E_UNKNOWN_CREDENTIALS)) errorLabel.setText(MessageService.message("samebug.component.authentication.logIn.error.email.unknown"));
            else if (errorCode.equals(LogIn.E_EMAIL_INVALID)) errorLabel.setText(MessageService.message("samebug.component.authentication.logIn.error.email.invalid"));
            else if (errorCode.equals(LogIn.E_EMAIL_LONG)) errorLabel.setText(MessageService.message("samebug.component.authentication.logIn.error.email.long"));
            else throw new ErrorCodeMismatchException(errorCode);
        }
    }

    final class PasswordField extends PasswordFormField {
        public PasswordField() {
            super(MessageService.message("samebug.component.authentication.password"));
        }

        @Override
        protected void updateErrorLabel(SamebugMultilineLabel errorLabel, String errorCode) throws ErrorCodeMismatchException {
            if (errorCode.equals(LogIn.E_UNKNOWN_CREDENTIALS)) errorLabel.setText(MessageService.message("samebug.component.authentication.logIn.error.password.unknown"));
            else if (errorCode.equals(LogIn.E_EMPTY_PASSWORD)) errorLabel.setText(MessageService.message("samebug.component.authentication.logIn.error.password.empty"));
            else throw new ErrorCodeMismatchException(errorCode);
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
