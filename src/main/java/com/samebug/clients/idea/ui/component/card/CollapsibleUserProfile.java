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
package com.samebug.clients.idea.ui.component.card;

import com.intellij.openapi.application.ApplicationManager;
import com.samebug.clients.common.entities.user.Statistics;
import com.samebug.clients.common.entities.user.User;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class CollapsibleUserProfile {
    final CollapsableView collapsable;
    final UserProfilePanel statisticsPanel;
    final JLabel openedStateLabel;
    final JLabel closedStateLabel;

    @Nullable
    User user;
    @Nullable
    Statistics statistics;

    public CollapsibleUserProfile() {
        openedStateLabel = new JLabel("hide") {
            {
                setHorizontalAlignment(SwingConstants.CENTER);
            }
        };
        closedStateLabel = new JLabel() {
            {
                setHorizontalAlignment(SwingConstants.CENTER);
            }
        };
        statisticsPanel = new UserProfilePanel();
        collapsable = new CollapsableView(statisticsPanel, closedStateLabel, openedStateLabel);
        collapsable.close();
        this.user = null;
        this.statistics = null;
        update();
    }

    public void updateUser(@Nullable User user) {
        this.user = user;
        update();
    }

    public void updateStatistics(@Nullable Statistics statistics) {
        this.statistics = statistics;
        update();
    }

    public JComponent getControl() {
        return collapsable;
    }

    private void update() {
        ApplicationManager.getApplication().assertIsDispatchThread();
        statisticsPanel.setProfile(user, statistics);
        String label;
        if (user == null) {
            label = "No user available";
        } else {
            if (statistics == null) {
                label = String.format("Logged in as %s (no statistics available)", user.getDisplayName());
            } else {
                label = String.format("Logged in as %s (%d marks | %d tips | %d thanks)",
                        user.getDisplayName(), statistics.getNumberOfMarks(), statistics.getNumberOfTips(), statistics.getNumberOfThanks());
            }
        }
        closedStateLabel.setText(label);
    }
}
