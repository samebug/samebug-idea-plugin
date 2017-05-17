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

import com.samebug.clients.common.ui.component.community.IHelpOthersCTA;
import com.samebug.clients.common.ui.component.hit.ITipHit;
import com.samebug.clients.common.ui.frame.solution.IWebResultsTab;
import org.jetbrains.annotations.NotNull;

public interface IHelpRequestTabs {
    void sentResponse(@NotNull ITipHit.Model tip);

    final class Model {
        public final IWebResultsTab.Model webResults;
        public final IHelpRequestTab.Model helpRequest;
        public final IHelpOthersCTA.Model cta;

        public Model(Model rhs) {
            this(rhs.webResults, rhs.helpRequest, rhs.cta);
        }

        public Model(IWebResultsTab.Model webResults, IHelpRequestTab.Model helpRequest, IHelpOthersCTA.Model cta) {
            this.webResults = webResults;
            this.helpRequest = helpRequest;
            this.cta = cta;
        }
    }
}
