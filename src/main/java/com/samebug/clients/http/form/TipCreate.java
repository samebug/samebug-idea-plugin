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

import com.samebug.clients.http.entities.jsonapi.JsonErrors;
import com.samebug.clients.http.exceptions.FormException;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class TipCreate {
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

    public abstract static class Base {
        public final String message;
        public final String sourceUrl;

        protected Base(@NotNull String message, @Nullable String sourceUrl) {
            this.message = message;
            this.sourceUrl = sourceUrl;
        }
    }

    public static final class ForExternalSolution extends Base {
        public final String type = "tip-externalsolution";
        public final Integer solutionId;

        public ForExternalSolution(@NotNull String message, @Nullable String sourceUrl, @NotNull Integer solutionId) {
            super(message, sourceUrl);
            this.solutionId = solutionId;
        }
    }

    public static final class ForSearch extends Base {
        public final String type = "tip-search";
        public final Integer searchId;

        public ForSearch(@NotNull String message, @Nullable String sourceUrl, @NotNull Integer searchId) {
            super(message, sourceUrl);
            this.searchId = searchId;
        }
    }

    public static final class ForHelpRequest extends Base {
        public final String type = "tip-helprequest";
        public final Integer searchId;
        public final String helpRequestId;

        public ForHelpRequest(@NotNull String message, @Nullable String sourceUrl, @NotNull Integer searchId, @NotNull String helpRequestId) {
            super(message, sourceUrl);
            this.searchId = searchId;
            this.helpRequestId = helpRequestId;
        }
    }

    public enum ErrorCode {
        TOO_SHORT
    }

    public static class BadRequest extends FormException {
        public final JsonErrors<ErrorCode> errorList;

        public BadRequest(JsonErrors<ErrorCode> errorList) {
            this.errorList = errorList;
        }

        public String toString() {
            return super.toString() + ": " + StringUtils.join(errorList.getErrorCodes(), ", ");
        }
    }

    private TipCreate() {}
}
