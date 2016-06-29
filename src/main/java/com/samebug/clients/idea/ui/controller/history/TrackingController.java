package com.samebug.clients.idea.ui.controller.history;

import com.intellij.openapi.project.Project;
import com.intellij.util.messages.MessageBusConnection;
import com.samebug.clients.idea.components.application.Tracking;
import com.samebug.clients.idea.messages.tracking.TrackingListener;
import com.samebug.clients.idea.messages.view.SearchGroupCardListener;
import com.samebug.clients.idea.tracking.Events;
import com.samebug.clients.idea.ui.controller.TabController;
import com.samebug.clients.search.api.entities.SearchGroup;
import org.jetbrains.annotations.NotNull;

final class TrackingController implements SearchGroupCardListener {
    @NotNull
    final HistoryTabController controller;
    @NotNull
    final Project myProject;
    @NotNull
    TrackingListener tracker;

    public TrackingController(@NotNull final HistoryTabController controller) {
        this.controller = controller;
        this.myProject = controller.project;
        tracker = Tracking.projectTracking(myProject);

        MessageBusConnection bus = myProject.getMessageBus().connect();
        bus.subscribe(SearchGroupCardListener.TOPIC, this);
    }

    @Override
    public void titleClick(@NotNull TabController tab, SearchGroup searchGroup) {
        if (controller == tab) {
            tracker.trace(Events.searchClick(myProject, searchGroup.getLastSearch().id));
        }
    }
}
