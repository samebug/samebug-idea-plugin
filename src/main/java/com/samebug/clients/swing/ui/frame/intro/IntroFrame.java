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
package com.samebug.clients.swing.ui.frame.intro;

import com.samebug.clients.common.ui.frame.IIntroFrame;
import com.samebug.clients.swing.ui.base.errorBarPane.BasicFrame;
import com.samebug.clients.swing.ui.base.label.SamebugLabel;

// TODO
public final class IntroFrame extends BasicFrame implements IIntroFrame {
    public IntroFrame() {
        addMainComponent(new SamebugLabel("TODO intro screen"));
    }
}
