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
package com.samebug.clients.common.ui.frame.helpRequest;

import java.net.URL;

public interface IHelpRequestHeader {
    final class Model {
        public final String title;
        public final String displayName;
        public final URL avatarUrl;

        public Model(Model rhs) {
            this(rhs.title, rhs.displayName, rhs.avatarUrl);
        }

        public Model(String title, String displayName, URL avatarUrl) {
            this.title = title;
            this.displayName = displayName;
            this.avatarUrl = avatarUrl;
        }
    }

    interface Listener {
        void clickViewStackTrace();
    }
}
