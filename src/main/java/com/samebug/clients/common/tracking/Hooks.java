/*
 * Copyright 2018 Samebug, Inc.
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
package com.samebug.clients.common.tracking;

public final class Hooks {
    public static final class Authentication {
        public static final String UNAUTHENTICATED = "unauthenticated";
    }

    public static final class Search {
        public static final String MENU = "ide-menu";
    }

    public static final class HelpRequest {
        public static final String ASK_BUGMATES = "bugmates-ask";
    }

    public static final class WriteTip {
        public static final String HELP_REQUEST_RESPONSE = "help-request-response";
        public static final String SEARCH = "search";
    }

    private Hooks() {}
}
