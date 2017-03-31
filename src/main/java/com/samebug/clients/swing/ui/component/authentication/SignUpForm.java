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
import com.samebug.clients.common.api.form.SignUp;
import com.samebug.clients.common.ui.component.authentication.ISignUpForm;
import com.samebug.clients.common.ui.component.form.ErrorCodeMismatchException;
import com.samebug.clients.common.ui.component.form.FieldNameMismatchException;
import com.samebug.clients.common.ui.component.form.FormMismatchException;
import com.samebug.clients.swing.ui.base.button.SamebugButton;
import com.samebug.clients.swing.ui.base.multiline.SamebugMultilineLabel;
import com.samebug.clients.swing.ui.modules.ListenerService;
import com.samebug.clients.swing.ui.modules.MessageService;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
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

        // TODO probably we should declare mig layout sizes explicitly in pixels, because the default somehow can get overridden to lpx (logical pixels) that leads to obscure bugs.
        setLayout(new MigLayout("fillx", "0[50%]20[50%]0", "0[]10[]10[]20[]0"));
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
    public void failPost(List<FieldError> errors) throws FormMismatchException {
        signUp.revertFromLoadingAnimation();

        List<FieldError> mismatched = new ArrayList<FieldError>();
        for (FieldError f : errors) {
            try {
                if (f.key.equals(SignUp.DISPLAY_NAME)) displayName.setFormError(f.code);
                else if (f.key.equals(SignUp.EMAIL)) email.setFormError(f.code);
                else if (f.key.equals(SignUp.PASSWORD)) password.setFormError(f.code);
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
        signUp.revertFromLoadingAnimation();
    }


    final class DisplayNameField extends FormField {
        public DisplayNameField() {
            super(MessageService.message("samebug.component.authentication.displayName"));
        }

        @Override
        protected void updateErrorLabel(SamebugMultilineLabel errorLabel, String errorCode) throws ErrorCodeMismatchException {
            if (errorCode.equals(SignUp.E_DISPLAY_NAME_LONG)) errorLabel.setText(MessageService.message("samebug.component.authentication.signUp.error.displayName.long"));
            else if (errorCode.equals(SignUp.E_DISPLAY_EMPTY)) errorLabel.setText(MessageService.message("samebug.component.authentication.signUp.error.displayName.empty"));
            else throw new ErrorCodeMismatchException(errorCode);
        }
    }

    final class EmailField extends FormField {
        public EmailField() {
            super(MessageService.message("samebug.component.authentication.email"));
        }

        @Override
        protected void updateErrorLabel(SamebugMultilineLabel errorLabel, String errorCode) throws ErrorCodeMismatchException {
            if (errorCode.equals(SignUp.E_EMAIL_TAKEN)) errorLabel.setText(MessageService.message("samebug.component.authentication.signUp.error.email.taken"));
            else if (errorCode.equals(SignUp.E_EMAIL_INVALID)) errorLabel.setText(MessageService.message("samebug.component.authentication.signUp.error.email.invalid"));
            else if (errorCode.equals(SignUp.E_EMAIL_LONG)) errorLabel.setText(MessageService.message("samebug.component.authentication.signUp.error.email.long"));
            else throw new ErrorCodeMismatchException(errorCode);
        }
    }

    final class PasswordField extends PasswordFormField {
        public PasswordField() {
            super(MessageService.message("samebug.component.authentication.password"));
        }

        @Override
        protected void updateErrorLabel(SamebugMultilineLabel errorLabel, String errorCode) throws ErrorCodeMismatchException {
            if (errorCode.equals(SignUp.E_PASSWORD_SHORT)) errorLabel.setText(MessageService.message("samebug.component.authentication.signUp.error.password.short"));
            else throw new ErrorCodeMismatchException(errorCode);
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
