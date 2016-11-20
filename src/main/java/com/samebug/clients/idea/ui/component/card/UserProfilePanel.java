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
import com.samebug.clients.common.search.api.entities.Author;
import com.samebug.clients.idea.ui.component.AvatarPanel;
import com.samebug.clients.idea.ui.component.TransparentPanel;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.awt.font.TextAttribute;
import java.util.HashMap;

final public class UserProfilePanel extends TransparentPanel {
    @Nullable
    User user;
    @Nullable
    Statistics statistics;

    public UserProfilePanel() {
        setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Color.black));
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
            add(new JLabel("No user information available") {
                {
                    setHorizontalAlignment(SwingConstants.CENTER);
                }
            });
        } else {
            // TODO move this somewhere else
            final Author author = new Author(user.getDisplayName(), null, user.getAvatarUrl());

            if (statistics == null) {
                add(new AvatarPanel(author), BorderLayout.WEST);
            } else {
                add(new AvatarPanel(author), BorderLayout.WEST);
                add(new StatisticsPanel(statistics), BorderLayout.EAST);
            }
        }
    }

    final class StatisticsPanel extends TransparentPanel {
        public StatisticsPanel(final Statistics statistics) {
            setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
            add(new ScorePanel("Marks", statistics.getNumberOfMarks()));
            add(new ScorePanel("Tips", statistics.getNumberOfTips()));
            add(new ScorePanel("Thanks", statistics.getNumberOfThanks()));
        }
    }

    final class ScorePanel extends TransparentPanel {
        public ScorePanel(String name, Integer score) {
            setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
            add(new JLabel(name) {
                {
                    setHorizontalAlignment(SwingConstants.CENTER);
                }
            }, BorderLayout.NORTH);
            add(new JLabel(score.toString()) {
                {
                    setHorizontalAlignment(SwingConstants.CENTER);
                    final HashMap<TextAttribute, Object> attributes = new HashMap<TextAttribute, Object>();
                    attributes.put(TextAttribute.SIZE, 16);
                    attributes.put(TextAttribute.WEIGHT, TextAttribute.WEIGHT_BOLD);
                    setFont(getFont().deriveFont(attributes));
                }
            });
        }
    }
}
