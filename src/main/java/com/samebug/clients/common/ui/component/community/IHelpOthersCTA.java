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

public interface IHelpOthersCTA {
    void startPostTip();

    void successPostTip();

    void failPostTipWithFormError(BadRequest errors);

    final class Model {
        public final int usersWaitingHelp;

        public Model(Model rhs) {
            this(rhs.usersWaitingHelp);
        }

        public Model(int usersWaitingHelp) {
            this.usersWaitingHelp = usersWaitingHelp;
        }
    }

    final class BadRequest {
        public BadRequest(TipBody tipBody) {
            this.tipBody = tipBody;
        }

        public final TipBody tipBody;

        public enum TipBody {
            TOO_SHORT, TOO_LONG
        }

    }

    interface Listener {
        void postTip(IHelpOthersCTA source, String tipBody);
    }
}
