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
package com.samebug.clients.swing.ui.component.hit;

import com.samebug.clients.common.ui.modules.TextService;
import com.samebug.clients.swing.ui.base.label.SamebugLabel;
import com.samebug.clients.swing.ui.modules.ColorService;
import com.samebug.clients.swing.ui.modules.FontService;

import java.util.Date;

final class TipTimestampLabel extends SamebugLabel implements com.samebug.clients.swing.ui.base.label.TimestampLabel {
    private final Date timestamp;

    TipTimestampLabel(Date timestamp) {
        this.timestamp = timestamp;
        setFont(FontService.regular(12));
        setForegroundColor(ColorService.UnemphasizedText);
        updateRelativeTimestamp();
    }

    @Override
    public void updateRelativeTimestamp() {
        setText(TextService.prettyTime(timestamp));
    }
}
