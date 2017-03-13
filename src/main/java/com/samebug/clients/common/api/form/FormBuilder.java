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

public final class FormBuilder {
    public static final class CreateHelpRequest {
        public static final String SEARCH_ID = "searchId";
        public static final String CONTEXT = "context";
    }

    public static final class CreateTip {
        public static final String BODY = "tip";
        public static final String E_TOO_SHORT = "MESSAGE_TOO_SHORT";
        public static final String E_TOO_LONG = "MESSAGE_TOO_LONG";
        public static final String E_NOT_YOUR_SEARCH = "NOT_YOUR_SEARCH";
        public static final String E_NOT_EXCEPTION_SEARCH = "NOT_EXCEPTION_SEARCH";
        public static final String E_UNKNOWN_SEARCH = "UNKNOWN_SEARCH";
        public static final String E_UNREACHABLE_SOURCE = "UNREACHABLE_SOURCE";
    }
}
