package com.samebug.clients.common.ui.component.authentication;

import com.samebug.clients.common.api.form.FieldError;
import com.samebug.clients.common.ui.component.form.FormMismatchException;

import java.util.List;

public interface ISignUpForm {
    void startPost();

    void failPost(List<FieldError> errors) throws FormMismatchException;

    void successPost();

    interface Listener {
        void signUp(ISignUpForm source, String displayName, String email, String password);
    }
}
