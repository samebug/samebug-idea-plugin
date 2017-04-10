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
package com.samebug.clients.common.ui.component.community;

import com.samebug.clients.http.form.FieldError;
import com.samebug.clients.common.ui.component.form.FormMismatchException;

import java.util.List;

public interface IAskForHelp {
    void startRequestTip();

    void failRequestTip(List<FieldError> errors) throws FormMismatchException;

    void successRequestTip();

    final class Model {
        public final int numberOfBugmates;
        public final String exceptionTitle;

        public Model(Model rhs) {
            this(rhs.numberOfBugmates, rhs.exceptionTitle);
        }

        public Model(int numberOfBugmates, String exceptionTitle) {
            this.numberOfBugmates = numberOfBugmates;
            this.exceptionTitle = exceptionTitle;
        }
    }

    interface Listener {
        void askBugmates(IAskForHelp source, String description);
    }
}
