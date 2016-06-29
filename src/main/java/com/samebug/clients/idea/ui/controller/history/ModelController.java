package com.samebug.clients.idea.ui.controller.history;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.util.messages.MessageBusConnection;
import com.samebug.clients.idea.messages.client.HistoryModelListener;
import com.samebug.clients.search.api.entities.SearchHistory;
import org.jetbrains.annotations.NotNull;

final class ModelController implements HistoryModelListener {
    final static Logger LOGGER = Logger.getInstance(ModelController.class);
    @NotNull
    final HistoryTabController controller;

    public ModelController(@NotNull final HistoryTabController controller) {
        this.controller = controller;

        MessageBusConnection projectMessageBus = controller.myProject.getMessageBus().connect(controller);
        projectMessageBus.subscribe(HistoryModelListener.TOPIC, this);
    }

    @Override
    public void start() {
        ApplicationManager.getApplication().invokeLater(new Runnable() {
            @Override
            public void run() {
                controller.view.setWarningLoading();
            }
        });
    }

    @Override
    public void success(final SearchHistory result) {
        controller.service.setHistory(result);
        ApplicationManager.getApplication().invokeLater(new Runnable() {
            @Override
            public void run() {
                controller.refreshTab();
            }
        });
    }

    @Override
    public void fail(Exception e) {
        controller.service.setHistory(null);
        ApplicationManager.getApplication().invokeLater(new Runnable() {
            @Override
            public void run() {
                controller.refreshTab();
            }
        });
    }

    @Override
    public void finish() {
    }
}
