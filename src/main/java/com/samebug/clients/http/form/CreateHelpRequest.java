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
package com.samebug.clients.http.form;

import com.samebug.clients.http.exceptions.FormException;
import com.samebug.clients.http.exceptions.SamebugException;
import com.samebug.clients.http.response.SamebugFormError;
import org.jetbrains.annotations.NotNull;

public final class CreateHelpRequest {
    public static final String SEARCH_ID = "searchId";
    public static final String CONTEXT = "context";
    public static final String E_TOO_LONG = "Maximum length is 160";
    public static final String E_NOT_YOUR_SEARCH = "NOT_YOUR_SEARCH";
    public static final String E_DUPLICATE_HELP_REQUEST = "DUPLICATE_HELP_REQUEST";
    public static final String E_NOT_STACKTRACE_SEARCH = "NOT_STACKTRACE_SEARCH";

    @NotNull
    public final Integer searchId;
    @NotNull
    public final String context;

    public CreateHelpRequest(@NotNull Integer searchId, @NotNull String context) {
        this.searchId = searchId;
        this.context = context;
    }

    public static final class Error implements SamebugFormError {}
    public static final class BadRequest extends FormException {
        public final Error error;

        public BadRequest(Error error) {
            this.error = error;
        }

    }

}
