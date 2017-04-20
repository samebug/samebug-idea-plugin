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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class CreateTip {
    public static final String BODY = "tip";
    public static final String E_TOO_SHORT = "MESSAGE_TOO_SHORT";
    public static final String E_TOO_LONG = "MESSAGE_TOO_LONG";
    public static final String E_NOT_YOUR_SEARCH = "NOT_YOUR_SEARCH";
    public static final String E_NOT_EXCEPTION_SEARCH = "NOT_EXCEPTION_SEARCH";
    public static final String E_UNKNOWN_SEARCH = "UNKNOWN_SEARCH";
    public static final String E_UNREACHABLE_SOURCE = "UNREACHABLE_SOURCE";
    public static final String E_UNRECOGNIZED_SOURCE = "UNRECOGNIZED_SOURCE";
    public static final String E_UNKNOWN_HELP_REQUEST = "UNKNOWN_HELP_REQUEST";
    public static final String E_NO_STACKTRACE = "NO_STACKTRACE";

    @NotNull
    public final Integer searchId;
    @NotNull
    public final String body;
    @Nullable
    public final String sourceUrl;
    @Nullable
    public final String helpRequestId;

    public CreateTip(@NotNull Integer searchId, @NotNull String body, @Nullable String sourceUrl, @Nullable String helpRequestId) {
        this.searchId = searchId;
        this.body = body;
        this.sourceUrl = sourceUrl;
        this.helpRequestId = helpRequestId;
    }

    public static class Error {

    }

    public static class BadRequest extends FormException {
        public final Error error;

        public BadRequest(Error error) {
            this.error = error;
        }
    }
}
