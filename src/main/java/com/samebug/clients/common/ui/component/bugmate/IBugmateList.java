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
package com.samebug.clients.common.ui.component.bugmate;

import java.util.List;

public interface IBugmateList {
    void startRequestTip();

    void interruptRequestTip();

    void successRequestTip(/*TODO param*/);

    final class Model {
        public final List<IBugmateHit.Model> bugmateHits;
        public final int numberOfOtherBugmates;
        public final boolean evenMoreExists;
        public final String exceptionTitle;

        public Model(Model rhs) {
            this(rhs.bugmateHits, rhs.numberOfOtherBugmates, rhs.evenMoreExists, rhs.exceptionTitle);
        }

        public Model(List<IBugmateHit.Model> bugmateHits, int numberOfOtherBugmates, boolean evenMoreExists, String exceptionTitle) {
            this.bugmateHits = bugmateHits;
            this.numberOfOtherBugmates = numberOfOtherBugmates;
            this.evenMoreExists = evenMoreExists;
            this.exceptionTitle = exceptionTitle;
        }
    }

    interface Listener {
        // TODO not sure if this is the right place for it, or the list and the ask should be separated.
        void askBugmates(IBugmateList source, String description);
    }
}
