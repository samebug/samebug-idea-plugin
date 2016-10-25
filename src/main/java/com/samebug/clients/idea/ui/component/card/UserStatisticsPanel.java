package com.samebug.clients.idea.ui.component.card;

import com.samebug.clients.idea.ui.component.TransparentPanel;
import com.samebug.clients.search.api.entities.UserStats;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

final public class UserStatisticsPanel extends TransparentPanel {
    @Nullable
    UserStats userStats;

    public UserStatisticsPanel() {
        add(new JLabel("No user information available"));
    }

    public void setUserStats(@NotNull UserStats userStats) {
        this.userStats = userStats;
        removeAll();
        add(new JLabel("Crashes: " + userStats.getNumberOfCrashes() +
                " | Marks: " + userStats.getNumberOfMarks() +
                " | Tips: " + userStats.getNumberOfTips() +
                " | Thanks: " + userStats.getNumberOfThanks()));
        revalidate();
        repaint();
    }
}
