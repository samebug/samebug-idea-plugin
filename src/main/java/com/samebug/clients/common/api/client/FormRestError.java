package com.samebug.clients.common.api.client;

import com.samebug.clients.common.api.form.FieldError;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class FormRestError extends BasicRestError {
    public final Map<String, List<String>> formErrors;

    public FormRestError(String code, String message, Map<String, List<String>> formErrors) {
        super(code, message);
        this.formErrors = formErrors;
    }

    public List<FieldError> getAllFieldErrors() {
        ArrayList<FieldError> list = new ArrayList<FieldError>();
        for (Map.Entry<String, List<String>> entry : formErrors.entrySet()) {
            for (String fieldError : entry.getValue()) {
                list.add(new FieldError(entry.getKey(), fieldError));
            }
        }
        return list;
    }

    public List<FieldError> getFieldErrors(String fieldName) {
        ArrayList<FieldError> list = new ArrayList<FieldError>();
        List<String> fieldErrors = formErrors.get(fieldName);
        if (fieldErrors != null) {
            for (String fieldError : fieldErrors) {
                list.add(new FieldError(fieldName, fieldError));
            }
        }
        return list;
    }
}
