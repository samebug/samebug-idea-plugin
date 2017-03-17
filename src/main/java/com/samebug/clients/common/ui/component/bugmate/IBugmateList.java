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

import com.samebug.clients.common.api.entities.helpRequest.MyHelpRequest;
import com.samebug.clients.common.api.form.FieldError;
import com.samebug.clients.common.ui.component.form.FormMismatchException;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface IBugmateList {
    void startRequestTip();

    void failRequestTip(List<FieldError> errors) throws FormMismatchException;

    void successRequestTip(/*TODO param*/);

    void startRevoke();

    void failRevoke();

    void successRevoke();

    final class Model {
        public final List<IBugmateHit.Model> bugmateHits;
        public final int numberOfOtherBugmates;
        public final boolean evenMoreExists;
        public final String exceptionTitle;
        // TODO this is a rest api entity in the ui model, remove it
        @Nullable
        public final MyHelpRequest helpRequest;

        public Model(Model rhs) {
            this(rhs.bugmateHits, rhs.numberOfOtherBugmates, rhs.evenMoreExists, rhs.exceptionTitle, rhs.helpRequest);
        }

        public Model(List<IBugmateHit.Model> bugmateHits, int numberOfOtherBugmates, boolean evenMoreExists, String exceptionTitle, @Nullable MyHelpRequest helpRequest) {
            this.bugmateHits = bugmateHits;
            this.numberOfOtherBugmates = numberOfOtherBugmates;
            this.evenMoreExists = evenMoreExists;
            this.exceptionTitle = exceptionTitle;
            this.helpRequest = helpRequest;
        }
    }

    interface Listener {
        // TODO not sure if this is the right place for it, or the list and the ask should be separated.
        // TODO It should definitely be separated.
        void askBugmates(IBugmateList source, String description);

        // TODO the UI should not know the help request id, it should come from the controller
        void revokeHelpRequest(IBugmateList source, String helpRequestId);
    }
}
