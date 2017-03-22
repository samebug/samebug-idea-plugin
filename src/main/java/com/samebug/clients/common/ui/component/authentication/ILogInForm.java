package com.samebug.clients.common.ui.component.authentication;

import com.samebug.clients.common.api.form.FieldError;
import com.samebug.clients.common.ui.component.form.FormMismatchException;

import java.util.List;

public interface ILogInForm {
    void startPost();

    void failPost(List<FieldError> errors) throws FormMismatchException;

    void successPost();

    interface Listener {
        void logIn(ILogInForm source, String email, String password);
        void forgotPassword(ILogInForm source);
    }
}
