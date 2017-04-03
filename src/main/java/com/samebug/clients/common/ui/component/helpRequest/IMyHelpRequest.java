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
package com.samebug.clients.common.ui.component.helpRequest;

import com.samebug.clients.common.api.entities.helpRequest.MyHelpRequest;
import org.jetbrains.annotations.NotNull;

public interface IMyHelpRequest {
    void startRevoke();

    void failRevoke();

    void successRevoke();

    final class Model {
        // TODO this is a rest api entity in the ui model, remove it
        @NotNull
        public final MyHelpRequest helpRequest;

        public Model(Model rhs) {
            this(rhs.helpRequest);
        }

        public Model(@NotNull MyHelpRequest helpRequest) {
            this.helpRequest = helpRequest;
        }
    }

    interface Listener {
        // TODO the UI should not know the help request id, it should come from the controller
        void revokeHelpRequest(IMyHelpRequest source, String helpRequestId);
    }
}
