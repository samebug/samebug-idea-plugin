/**
 * Copyright 2016 Samebug, Inc.
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
package com.samebug.clients.idea.ui.component.organism;

import com.samebug.clients.common.search.api.entities.SearchGroup;
import com.samebug.clients.common.ui.TextUtil;
import com.samebug.clients.idea.ui.ColorUtil;
import com.samebug.clients.idea.ui.component.TransparentPanel;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;

final public class GroupInfoPanel extends TransparentPanel {

    public GroupInfoPanel(@NotNull final SearchGroup searchGroup) {
        setLayout(new FlowLayout(FlowLayout.RIGHT));
        add(new InfoLabel(searchGroup));
    }

    public void refreshDateLabels(@NotNull final SearchGroup searchGroup) {
        removeAll();
        add(new InfoLabel(searchGroup));
    }

    class InfoLabel extends JLabel {
        InfoLabel(@NotNull final SearchGroup searchGroup) {
            String text;
            if (searchGroup.getNumberOfSearches() == 1) {
                text = String.format("%s", TextUtil.prettyTime(searchGroup.getLastSeen()));
            } else {
                text = String.format("%s | %d times, first %s",
                        TextUtil.prettyTime(searchGroup.getLastSeen()), searchGroup.getNumberOfSearches(), TextUtil.prettyTime(searchGroup.getFirstSeen()));
            }

            setText(text);
        }

        @Override
        public Color getForeground() {
            return ColorUtil.unemphasizedText();
        }
    }
}
