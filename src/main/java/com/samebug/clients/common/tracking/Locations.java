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

import org.jetbrains.annotations.NotNull;

public final class Locations {

    public static final class Search extends Base {
        public final Integer searchId;

        public Search(@NotNull Integer searchId) {
            super("stack-trace-search");
            this.searchId = searchId;
        }
    }

    public static final class HelpRequest extends Base {
        public final String helpRequestId;

        public HelpRequest(@NotNull String helpRequestId) {
            super("help-request");
            this.helpRequestId = helpRequestId;
        }
    }

    public static final class HelpRequestList extends Base {
        public HelpRequestList() {
            super("incoming-help-requests");
        }
    }

    public static final class Authentication extends Base {
        public Authentication() {
            super("authentication");
        }
    }

    public static final class HelpRequestNotification extends Base {
        public final String helpRequestId;

        public HelpRequestNotification(@NotNull String helpRequestId) {
            super("help-request-notification");
            this.helpRequestId = helpRequestId;
        }
    }

    public static final class TipAnswerNotification extends Base {
        public final Integer solutionId;

        public TipAnswerNotification(@NotNull Integer solutionId) {
            super("tip-answer-notification");
            this.solutionId = solutionId;
        }
    }

    public static final class SearchDialog extends Base {
        public SearchDialog() {
            super("search-dialog");
        }
    }


    public abstract static class Base {
        public final String type;
        public String tabId;

        protected Base(String type) {
            this.type = type;
        }
    }

    private Locations() {

    }
}
