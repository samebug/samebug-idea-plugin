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
import org.jetbrains.annotations.Nullable;

public interface IMarkButton {

    void setLoading();

    void update(Model model);

    final class Model {
        public final int marks;
        @Nullable
        public final Integer userMarkId;
        public final boolean userCanMark;

        public Model(Model rhs) {
            this(rhs.marks, rhs.userMarkId, rhs.userCanMark);
        }

        public Model(int marks, @Nullable Integer userMarkId, boolean userCanMark) {
            this.marks = marks;
            this.userMarkId = userMarkId;
            this.userCanMark = userCanMark;
        }
    }

    interface Listener {
        Topic<Listener> TOPIC = Topic.create("IMarkButton", Listener.class);

        void markClicked(IMarkButton markButton, Integer solutionId, Integer currentMarkId);
    }

}
