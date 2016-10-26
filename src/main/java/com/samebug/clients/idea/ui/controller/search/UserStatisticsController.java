package com.samebug.clients.idea.ui.controller.search;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.util.messages.MessageBusConnection;
import com.samebug.clients.idea.messages.client.UserStatsListener;
import com.samebug.clients.search.api.entities.UserStats;
import org.jetbrains.annotations.NotNull;

public class UserStatisticsController implements UserStatsListener {
    final static Logger LOGGER = Logger.getInstance(UserStatisticsController.class);
    @NotNull
    final SearchTabController controller;

    public UserStatisticsController(@NotNull final SearchTabController controller) {
        this.controller = controller;

        MessageBusConnection projectConnection = controller.project.getMessageBus().connect(controller);
        projectConnection.subscribe(UserStatsListener.TOPIC, this);
    }

    @Override
    public void successGetUserStats(int userId, int workspaceId, final UserStats userStats) {
        ApplicationManager.getApplication().invokeLater(new Runnable() {
            @Override
            public void run() {
                controller.view.userStatisticsPanel.setUserStats(userStats);
                controller.view.revalidate();
                controller.view.repaint();
            }
        });
    }

    @Override
    public void failGetUserStats(int userId, int workspaceId, Exception e) {

    }
}
