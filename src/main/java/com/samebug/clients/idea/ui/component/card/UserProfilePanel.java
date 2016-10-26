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

import com.samebug.clients.common.entities.user.Statistics;
import com.samebug.clients.common.entities.user.User;
import com.samebug.clients.idea.ui.component.TransparentPanel;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

final public class UserProfilePanel extends TransparentPanel {
    @Nullable
    User user;
    @Nullable
    Statistics statistics;

    public UserProfilePanel() {
        update();
    }

    public void setProfile(@Nullable User user, @Nullable Statistics statistics) {
        this.user = user;
        this.statistics = statistics;
        update();
    }

    private void update() {
        removeAll();
        if (user == null) {
            add(new JLabel("No user information available"));
        } else {
            // TODO show profile, etc
            if (statistics == null) {
                add(new JLabel("User " + user.getDisplayName()));
            } else {
                add(new JLabel("Marks: " + statistics.getNumberOfMarks() +
                        " | Tips: " + statistics.getNumberOfTips() +
                        " | Thanks: " + statistics.getNumberOfThanks()));

            }
//            revalidate();
//            repaint();
        }
    }
}
