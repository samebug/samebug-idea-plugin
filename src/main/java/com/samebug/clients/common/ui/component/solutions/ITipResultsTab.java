/**
 * Copyright 2017 Samebug, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.samebug.clients.common.ui.component.solutions;

import com.intellij.util.messages.Topic;

import java.util.List;

public interface ITipResultsTab {
    final class Model {
        public final List<ITipHit.Model> tipHits;
        public final IBugmateList.Model bugmateList;

        public Model(Model rhs) {
            this(rhs.tipHits, rhs.bugmateList);
        }

        public Model(List<ITipHit.Model> tipHits, IBugmateList.Model bugmateList) {
            this.tipHits = tipHits;
            this.bugmateList = bugmateList;
        }

        public int getTipsSize() {
            return tipHits.size();
        }
    }

    interface Listener {
        Topic<Listener> TOPIC = Topic.create("ITipResultsTab", Listener.class);
    }
}
