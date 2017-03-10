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
package com.samebug.clients.swing.ui.frame.tipRequestList;

import com.samebug.clients.common.ui.frame.tipRequestList.ITipRequestListHeader;
import com.samebug.clients.swing.ui.base.label.SamebugLabel;
import com.samebug.clients.swing.ui.modules.ColorService;
import com.samebug.clients.swing.ui.modules.FontService;
import com.samebug.clients.swing.ui.modules.MessageService;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;

// TODO extract HitsLabel from tabbed pane and use that here for the number
public final class TipRequestListHeader extends JComponent implements ITipRequestListHeader {
    public TipRequestListHeader(Model model) {
        final SamebugLabel title = new SamebugLabel(MessageService.message("samebug.frame.tipRequestList.title"), FontService.demi(24));
        title.setForeground(ColorService.EmphasizedText);

        setLayout(new MigLayout("fillx", "20[]0", "25[]27"));
        add(title);
    }
}
