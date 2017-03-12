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
package com.samebug.clients.common.ui.frame.helpRequestList;

import com.samebug.clients.common.ui.component.profile.IProfilePanel;
import com.samebug.clients.common.ui.frame.IFrame;

public interface IHelpRequestListFrame extends IFrame {
    void loadingSucceeded(Model model);

    void setLoading();

    final class Model {
        public final IHelpRequestListHeader.Model header;
        public final IHelpRequestList.Model requestList;
        public final IProfilePanel.Model profilePanel;

        public Model(Model rhs) {
            this(rhs.header, rhs.requestList, rhs.profilePanel);
        }

        public Model(IHelpRequestListHeader.Model header, IHelpRequestList.Model requestList, IProfilePanel.Model profilePanel) {
            this.header = header;
            this.requestList = requestList;
            this.profilePanel = profilePanel;
        }
    }

    interface Listener extends FrameListener {
    }
}
