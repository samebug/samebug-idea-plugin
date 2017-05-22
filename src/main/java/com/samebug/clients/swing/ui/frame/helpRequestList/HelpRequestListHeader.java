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
package com.samebug.clients.swing.ui.frame.helpRequestList;

import com.samebug.clients.common.ui.frame.helpRequestList.IHelpRequestListHeader;
import com.samebug.clients.common.ui.modules.MessageService;
import com.samebug.clients.swing.ui.base.label.SamebugLabel;
import com.samebug.clients.swing.ui.base.tabbedPane.HitsLabel;
import com.samebug.clients.swing.ui.modules.ColorService;
import com.samebug.clients.swing.ui.modules.FontService;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;

public final class HelpRequestListHeader extends JComponent implements IHelpRequestListHeader {
    public HelpRequestListHeader(Model model) {
        final JComponent title = new Title();
        final JComponent hits = new HelpRequests(model.numberOfHelpRequests);

        setLayout(new MigLayout("", "20px[]5px[]0", "25px[0!]4px[]27px"));
        add(title, "cell 0 0, spany 2");
//        add(filler, "cell 1 0, top, flowy");
        add(hits, "cell 1 1, top");
    }

    final class Title extends SamebugLabel {
        {
            setText(MessageService.message("samebug.frame.helpRequestList.title"));
            setFont(FontService.demi(24));
            setForegroundColor(ColorService.EmphasizedText);
        }
    }

    final class HelpRequests extends HitsLabel {
        public HelpRequests(int n) {
            super(HitsLabel.LARGE);
            setText(Integer.toString(n));
            setForegroundColor(ColorService.SelectedTab);
        }
    }
}
