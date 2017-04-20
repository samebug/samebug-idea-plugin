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

import com.samebug.clients.common.ui.component.authentication.ILogInForm;
import com.samebug.clients.idea.tracking.Events;
import com.samebug.clients.swing.ui.base.button.SamebugButton;
import com.samebug.clients.swing.ui.base.label.LinkLabel;
import com.samebug.clients.swing.ui.base.multiline.SamebugMultilineLabel;
import com.samebug.clients.swing.ui.modules.FontService;
import com.samebug.clients.swing.ui.modules.ListenerService;
import com.samebug.clients.swing.ui.modules.MessageService;
import com.samebug.clients.swing.ui.modules.TrackingService;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

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
                if (logIn.isEnabled()) sendForm();
            }
        });
        forgotPassword.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (forgotPassword.isEnabled()) {
                    getListener().forgotPassword(LogInForm.this);
                    TrackingService.trace(Events.registrationForgottenPasswordClicked());
                }
            }
        });
        FormActionListener formActionListener = new FormActionListener();
        email.addActionListener(formActionListener);
        password.addActionListener(formActionListener);

        setLayout(new MigLayout("fillx", ":push[50%]20px[50%]:push", "0[]10px[]20px[]0"));
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
    public void failPost(BadRequest errors) {
        logIn.revertFromLoadingAnimation();
//        if (f.key.equals(LogIn.EMAIL)) email.setFormError(f.code);
//        else if (f.key.equals(LogIn.PASSWORD)) password.setFormError(f.code);
//        TrackingService.trace(Events.registrationError("SignIn", errors));
    }

    @Override
    public void successPost() {
        logIn.revertFromLoadingAnimation();
    }


    private void sendForm() {
        getListener().logIn(LogInForm.this, email.getText(), password.getText());
        TrackingService.trace(Events.registrationSend("credentials", "SignIn"));
    }

    final class EmailField extends FormField<ILogInForm.BadRequest.Email> {
        public EmailField() {
            super(MessageService.message("samebug.component.authentication.email"));
        }

        @Override
        protected void updateErrorLabel(SamebugMultilineLabel errorLabel, ILogInForm.BadRequest.Email errorCode) {
            switch (errorCode) {
                case UNKNOWN_CREDENTIALS:
                    errorLabel.setText(MessageService.message("samebug.component.authentication.logIn.error.email.unknown"));
                    break;
            }
        }
    }

    final class PasswordField extends PasswordFormField<ILogInForm.BadRequest.Password> {
        public PasswordField() {
            super(MessageService.message("samebug.component.authentication.password"));
        }

        @Override
        protected void updateErrorLabel(SamebugMultilineLabel errorLabel, ILogInForm.BadRequest.Password errorCode) {
            switch (errorCode) {
                case UNKNOWN_CREDENTIALS:
                    errorLabel.setText(MessageService.message("samebug.component.authentication.logIn.error.password.unknown"));
                    break;
            }
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

    private class FormActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            sendForm();
        }
    }

    Listener getListener() {
        return ListenerService.getListener(this, Listener.class);
    }

}
