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
package com.samebug.clients.common.ui.frame.tipRequest;

import com.samebug.clients.common.ui.component.hit.ITipHit;
import com.samebug.clients.common.ui.component.tipRequest.ITipRequest;

import java.util.List;

public interface ITipRequestTab {
    final class Model {
        public final ITipRequest.Model tipRequest;
        public final List<ITipHit.Model> tipHits;

        public Model(Model rhs) {
            this(rhs.tipHits, rhs.tipRequest);
        }

        public Model(List<ITipHit.Model> tipHits, ITipRequest.Model tipRequest) {
            this.tipHits = tipHits;
            this.tipRequest = tipRequest;
        }
    }

    interface Listener {
        void sendTip(String tipBody);

        void clickExplanation();
    }
}
