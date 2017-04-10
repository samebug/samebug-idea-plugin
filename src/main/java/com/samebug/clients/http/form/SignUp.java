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

import org.jetbrains.annotations.NotNull;

public final class SignUp {
    public static final String DISPLAY_NAME = "displayName";
    public static final String EMAIL = "email";
    public static final String PASSWORD = "password";
    public static final String NEWSLETTER = "subscribedToNewsLetter";
    public static final String E_EMAIL_INVALID = "Invalid email address";
    public static final String E_EMAIL_LONG = "Email must not be more than 64 characters long";
    public static final String E_EMAIL_TAKEN = "There is already an account using this email";
    public static final String E_PASSWORD_SHORT = "Password must be at least 6 characters long";
    public static final String E_DISPLAY_NAME_LONG = "Display name must not be more than 32 characters long";
    public static final String E_DISPLAY_EMPTY = "This field is required";

    @NotNull
    public final String displayName;
    @NotNull
    public final String email;
    @NotNull
    public final String password;
    @NotNull
    public final Boolean subscribedToNewsLetter;

    public SignUp(@NotNull String displayName, @NotNull String email, @NotNull String password) {
        this.displayName = displayName;
        this.email = email;
        this.password = password;
        this.subscribedToNewsLetter = false;
    }
}
