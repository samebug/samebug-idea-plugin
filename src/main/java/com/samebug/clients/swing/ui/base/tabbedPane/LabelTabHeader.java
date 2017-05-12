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
package com.samebug.clients.swing.ui.base.tabbedPane;

import com.samebug.clients.swing.ui.base.label.SamebugLabel;
import com.samebug.clients.swing.ui.modules.FontService;
import net.miginfocom.swing.MigLayout;

public class LabelTabHeader extends SamebugTabHeader {
    protected final SamebugLabel tabLabel;

    public LabelTabHeader(String tabName) {
        tabLabel = new SamebugLabel(tabName, FontService.demi(16));

        setLayout(new MigLayout("", "0[]0", "0[20px, fill]0"));
        add(tabLabel, "cell 0 0");

        updateColors();
    }
}
