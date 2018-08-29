/*
 * Copyright 2018 Samebug, Inc.
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

import com.samebug.clients.swing.tracking.TrackingKeys;
import com.samebug.clients.swing.ui.base.label.SamebugLabel;
import com.samebug.clients.swing.ui.modules.ColorService;
import com.samebug.clients.swing.ui.modules.DataService;
import com.samebug.clients.swing.ui.modules.FontService;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;

public class LabelAndHitsTabHeader extends SamebugTabHeader {
    protected final SamebugLabel tabLabel;
    protected final HitsLabel hitsLabel;
    protected Color[] selectedHitColor = ColorService.SelectedTab;

    public LabelAndHitsTabHeader(String tabName, int hits) {
        DataService.putData(this, TrackingKeys.Label, tabName);
        tabLabel = new SamebugLabel(tabName, FontService.demi(16));
        hitsLabel = new HitsLabel(HitsLabel.SMALL);
        setHits(hits);

        setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 20));
        setLayout(new MigLayout("", "0[]7px[]0", "0[20px, fill]0"));
        add(tabLabel, "cell 0 0");
        add(hitsLabel, "cell 1 0");

        updateColors();
    }

    public void setHits(int hits) {
        hitsLabel.setText(Integer.toString(hits));
    }

    @Override
    protected void updateColors() {
        super.updateColors();

        // hit label in selected state has a visually corrected color
        if (hitsLabel != null && selected) hitsLabel.setForeground(ColorService.forCurrentTheme(selectedHitColor));
    }
}
