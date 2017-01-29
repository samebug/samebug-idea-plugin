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
package com.samebug.clients.idea.ui.component.card;

import com.samebug.clients.common.search.api.entities.SearchGroup;
import com.samebug.clients.idea.ui.component.organism.GroupInfoPanel;

import javax.swing.*;

public abstract class SearchGroupCard extends JPanel {
    public GroupInfoPanel groupInfoPanel;

    public SearchGroupCard(SearchGroup group) {
        this.groupInfoPanel = new GroupInfoPanel(group);
    }

    public void refreshDateLabels() {
        groupInfoPanel.refreshDateLabels(getSearchGroup());
    }

    abstract SearchGroup getSearchGroup();

    public interface Actions {
        String getTitleMouseOverText(SearchGroup group);

        void onClickTitle(SearchGroup group);
    }
}
