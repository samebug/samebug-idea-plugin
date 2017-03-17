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
