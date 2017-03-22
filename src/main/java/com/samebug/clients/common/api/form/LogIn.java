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
package com.samebug.clients.common.api.form;

import org.jetbrains.annotations.NotNull;

public final class LogIn {
    public static final String EMAIL = "email";
    public static final String PASSWORD = "password";
    public static final String E_UNKNOWN_CREDENTIALS = "UNKNOWN_CREDENTIALS";
    public static final String E_EMPTY_PASSWORD = "This field is required";
    public static final String E_EMAIL_INVALID = "Invalid email address";
    public static final String E_EMAIL_LONG = "Email must not be more than 64 characters long";

    @NotNull
    public final String email;
    @NotNull
    public final String password;

    public LogIn(@NotNull String email, @NotNull String password) {
        this.email = email;
        this.password = password;
    }
}
