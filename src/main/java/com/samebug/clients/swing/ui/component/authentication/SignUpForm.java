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

import com.intellij.openapi.diagnostic.Logger;
import com.samebug.clients.common.ui.component.authentication.ISignUpForm;
import com.samebug.clients.idea.tracking.Events;
import com.samebug.clients.swing.ui.base.button.SamebugButton;
import com.samebug.clients.swing.ui.base.multiline.SamebugMultilineLabel;
import com.samebug.clients.swing.ui.modules.ListenerService;
import com.samebug.clients.swing.ui.modules.MessageService;
import com.samebug.clients.swing.ui.modules.TrackingService;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public final class SignUpForm extends JComponent implements ISignUpForm {
    private static final Logger LOGGER = Logger.getInstance(SignUpForm.class);
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
                if (signUp.isEnabled()) sendForm();
            }
        });

        FormActionListener formActionListener = new FormActionListener();
        displayName.addActionListener(formActionListener);
        email.addActionListener(formActionListener);
        password.addActionListener(formActionListener);

        setLayout(new MigLayout("fillx", "0[50%]20px[50%]0", "0[]10px[]10px[]20px[]0"));
        add(displayName, "cell 0 0, spanx 2, growx");
        add(email, "cell 0 1, spanx 2, growx");
        add(password, "cell 0 2, spanx 2, growx");
        add(signUp, "cell 0 3, growx");
    }

    @Override
    public void startPost() {
        signUp.changeToLoadingAnimation();
    }

    @Override
    public void failPost(BadRequest errors) {
        signUp.revertFromLoadingAnimation();
//        if (f.key.equals(SignUp.DISPLAY_NAME)) displayName.setFormError(f.code);
//        else if (f.key.equals(SignUp.EMAIL)) email.setFormError(f.code);
//        else if (f.key.equals(SignUp.PASSWORD)) password.setFormError(f.code);
//        TrackingService.trace(Events.registrationError("SignUp", errors));
    }

    @Override
    public void successPost() {
        signUp.revertFromLoadingAnimation();
    }


    private void sendForm() {
        getListener().signUp(SignUpForm.this, displayName.getText(), email.getText(), password.getText());
        TrackingService.trace(Events.registrationSend("credentials", "SignUp"));
    }

    final class DisplayNameField extends FormField<ISignUpForm.BadRequest.DisplayName> {
        DisplayNameField() {
            super(MessageService.message("samebug.component.authentication.displayName"));
        }

        @Override
        protected void updateErrorLabel(SamebugMultilineLabel errorLabel, ISignUpForm.BadRequest.DisplayName errorCode) {
            switch (errorCode) {
                case EMPTY:
                    errorLabel.setText(MessageService.message("samebug.component.authentication.signUp.error.displayName.empty"));
                    break;
                case TOO_LONG:
                    errorLabel.setText(MessageService.message("samebug.component.authentication.signUp.error.displayName.long"));
                    break;
                default:
                    LOGGER.warn("Unhandled error code " + errorCode);
            }
        }
    }

    final class EmailField extends FormField<ISignUpForm.BadRequest.Email> {
        EmailField() {
            super(MessageService.message("samebug.component.authentication.email"));
        }

        @Override
        protected void updateErrorLabel(SamebugMultilineLabel errorLabel, BadRequest.Email errorCode) {
            switch (errorCode) {
                case TAKEN:
                    errorLabel.setText(MessageService.message("samebug.component.authentication.signUp.error.email.taken"));
                    break;
                case INVALID:
                    errorLabel.setText(MessageService.message("samebug.component.authentication.signUp.error.email.invalid"));
                    break;
                case LONG:
                    errorLabel.setText(MessageService.message("samebug.component.authentication.signUp.error.email.long"));
                    break;
                default:
                    LOGGER.warn("Unhandled error code " + errorCode);
            }
        }
    }

    final class PasswordField extends PasswordFormField<ISignUpForm.BadRequest.Password> {
        PasswordField() {
            super(MessageService.message("samebug.component.authentication.password"));
        }

        @Override
        protected void updateErrorLabel(SamebugMultilineLabel errorLabel, ISignUpForm.BadRequest.Password errorCode) {
            switch (errorCode) {
                case SHORT:
                    errorLabel.setText(MessageService.message("samebug.component.authentication.signUp.error.password.short"));
                    break;
                default:
                    LOGGER.warn("Unhandled error code " + errorCode);
            }
        }
    }

    final class SignUpButton extends SamebugButton {
        {
            setFilled(true);
            setText(MessageService.message("samebug.component.authentication.signUp"));
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
