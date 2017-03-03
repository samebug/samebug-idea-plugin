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
import com.samebug.clients.common.ui.component.profile.IProfilePanel;
import com.samebug.clients.common.ui.component.util.IFrame;

public interface ISolutionFrame extends IFrame {
    void setLoading();

    void loadingSucceeded(Model model);

    void loadingFailedWithAuthenticationError();

    void loadingFailedWithAuthorizationError();

    void loadingFailedWithRetriableError();

    void loadingFailedWithNetworkError();

    void loadingFailedWithServerError();

    void loadingFailedWithGenericError();

    final class Model {
        public final IExceptionHeaderPanel.Model header;
        public final IResultTabs.Model resultTabs;
        public final IProfilePanel.Model profilePanel;

        public Model(Model rhs) {
            this(rhs.resultTabs, rhs.header, rhs.profilePanel);
        }

        public Model(IResultTabs.Model resultTabs, IExceptionHeaderPanel.Model header, IProfilePanel.Model profilePanel) {
            this.resultTabs = resultTabs;
            this.header = header;
            this.profilePanel = profilePanel;
        }
    }

    interface Listener {
        Topic<Listener> TOPIC = Topic.create("ISolutionFrame", Listener.class);

        void reload();

        void openSamebugSettings();

        void openNetworkSettings();
    }
}
