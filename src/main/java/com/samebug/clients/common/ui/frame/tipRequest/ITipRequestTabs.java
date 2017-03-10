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
package com.samebug.clients.common.ui.frame.tipRequest;

import com.samebug.clients.common.ui.component.community.IHelpOthersCTA;
import com.samebug.clients.common.ui.frame.solution.IWebResultsTab;

public interface ITipRequestTabs {
    final class Model {
        public final IWebResultsTab.Model webResults;
        public final ITipRequestTab.Model tipRequest;
        public final IHelpOthersCTA.Model cta;

        public Model(Model rhs) {
            this(rhs.webResults, rhs.tipRequest, rhs.cta);
        }

        public Model(IWebResultsTab.Model webResults, ITipRequestTab.Model tipRequest, IHelpOthersCTA.Model cta) {
            this.webResults = webResults;
            this.tipRequest = tipRequest;
            this.cta = cta;
        }
    }
}
