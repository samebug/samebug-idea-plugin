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
package com.samebug.clients.swing.ui.frame.tipRequestList;

import com.samebug.clients.common.ui.frame.tipRequestList.ITipRequestListHeader;
import com.samebug.clients.swing.ui.base.label.SamebugLabel;
import com.samebug.clients.swing.ui.base.tabbedPane.HitsLabel;
import com.samebug.clients.swing.ui.modules.ColorService;
import com.samebug.clients.swing.ui.modules.FontService;
import com.samebug.clients.swing.ui.modules.MessageService;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;

public final class TipRequestListHeader extends JComponent implements ITipRequestListHeader {
    public TipRequestListHeader(Model model) {
        final JComponent title = new Title();
        final JComponent hits = new TipRequests(model.numberOfTipRequests);

        setLayout(new MigLayout("", "20[]5[]0", "25[0!]4[]27"));
        add(title, "cell 0 0, spany 2");
//        add(filler, "cell 1 0, top, flowy");
        add(hits, "cell 1 1, top");
    }

    final class Title extends SamebugLabel {
        {
            setText(MessageService.message("samebug.frame.tipRequestList.title"));
            setFont(FontService.demi(24));
            setForegroundColor(ColorService.EmphasizedText);
        }
    }

    final class TipRequests extends HitsLabel {
        public TipRequests(int n) {
            super(HitsLabel.LARGE);
            setText(Integer.toString(n));
            setForegroundColor(ColorService.SelectedTab);
        }
    }
}
