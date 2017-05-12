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

public final class SignUp {
    public static final class Data {
        public final String type;
        @NotNull
        public final String displayName;
        @NotNull
        public final String email;
        @NotNull
        public final String password;

        public Data(@NotNull String displayName, @NotNull String email, @NotNull String password) {
            type = "signup-request";
            this.displayName = displayName;
            this.email = email;
            this.password = password;
        }
    }

    public enum ErrorCode {
        EMAIL_USED, EMAIL_INVALID, EMAIL_LONG,
        DISPLAYNAME_LONG, DISPLAYNAME_EMPTY,
        PASSWORD_EMPTY
    }

    public static final class BadRequest extends FormException {
        public final JsonErrors<ErrorCode> errorList;

        public BadRequest(JsonErrors<ErrorCode> errorList) {
            this.errorList = errorList;
        }

        public String toString() {
            return super.toString() + ": " + StringUtils.join(errorList.getErrorCodes(), ", ");
        }
    }
}
