package com.samebug.clients.idea.ui.controller.history;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.util.messages.MessageBusConnection;
import com.samebug.clients.idea.components.application.ClientService;
import com.samebug.clients.idea.components.application.IdeaSamebugPlugin;
import com.samebug.clients.idea.messages.view.HistoryViewListener;
import com.samebug.clients.search.api.exceptions.SamebugClientException;
import org.jetbrains.annotations.NotNull;

final class ViewController implements HistoryViewListener {
    final static Logger LOGGER = Logger.getInstance(ModelController.class);
    @NotNull
    final HistoryTabController controller;

    public ViewController(@NotNull final HistoryTabController controller) {
        this.controller = controller;

        MessageBusConnection projectConnection = controller.myProject.getMessageBus().connect(controller);
        projectConnection.subscribe(HistoryViewListener.TOPIC, this);
    }

    @Override
    public void setZeroSolutionFilter(boolean showZeroSolutionSearches) {
        controller.service.setShowZeroSolutionSearches(showZeroSolutionSearches);
        controller.refreshTab();
    }

    @Override
    public void setRecurringFilter(boolean showRecurringSearches) {
        controller.service.setShowRecurringSearches(showRecurringSearches);
        controller.refreshTab();
    }

    @Override
    public void reload() {
        ApplicationManager.getApplication().executeOnPooledThread(new Runnable() {
            @Override
            public void run() {
                ClientService client = IdeaSamebugPlugin.getInstance().getClient();
                try {
                    client.getSearchHistory();
                } catch (SamebugClientException e1) {
                    LOGGER.warn("Failed to download search history", e1);
                }
            }
        });
    }

}
