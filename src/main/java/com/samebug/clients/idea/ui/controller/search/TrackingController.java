package com.samebug.clients.idea.ui.controller.search;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.util.messages.MessageBusConnection;
import com.samebug.clients.idea.components.application.IdeaSamebugPlugin;
import com.samebug.clients.idea.components.application.Tracking;
import com.samebug.clients.idea.messages.tracking.TrackingListener;
import com.samebug.clients.idea.messages.view.MarkViewListener;
import com.samebug.clients.idea.messages.view.SearchGroupCardListener;
import com.samebug.clients.idea.messages.view.WriteTipListener;
import com.samebug.clients.idea.tracking.Events;
import com.samebug.clients.idea.ui.component.organism.MarkPanel;
import com.samebug.clients.idea.ui.controller.TabController;
import com.samebug.clients.search.api.entities.SearchGroup;
import org.jetbrains.annotations.NotNull;

import java.net.URL;

final public class TrackingController implements SearchGroupCardListener, MarkViewListener, WriteTipListener {
    @NotNull
    final SearchTabController controller;
    @NotNull
    final Project myProject;
    @NotNull
    TrackingListener tracker;

    public TrackingController(@NotNull final SearchTabController controller) {
        this.controller = controller;
        this.myProject = controller.project;
        tracker = Tracking.projectTracking(myProject);

        MessageBusConnection bus = myProject.getMessageBus().connect();
        bus.subscribe(SearchGroupCardListener.TOPIC, this);
        bus.subscribe(MarkViewListener.TOPIC, this);
        bus.subscribe(WriteTipListener.TOPIC, this);
    }

    @Override
    public void titleClick(@NotNull TabController tab, SearchGroup searchGroup) {
        if (controller == tab) {
            // TODO there should be no logic/work here. If the url is not ready, than it is not a linkClicked event.
            URL url = IdeaSamebugPlugin.getInstance().getUrlBuilder().search(searchGroup.getLastSearch().id);
            tracker.trace(Events.linkClick(myProject, url));
        }
    }

    @Override
    public void ctaClick(TabController tab) {
        if (controller == tab) {
            tracker.trace(Events.writeTipOpen(myProject, controller.mySearchId));
        }
    }

    @Override
    public void cancelClick(TabController tab) {
        if (controller == tab) {
            tracker.trace(Events.writeTipCancel(myProject, controller.mySearchId));
        }
    }

    @Override
    public void sendClick(TabController tab, String tip, String rawSourceUrl) {
        if (controller == tab) {
            tracker.trace(Events.writeTipSubmit(myProject, controller.mySearchId, tip, rawSourceUrl, null));
        }
    }

    @Override
    public void mark(TabController tab, MarkPanel.Model model) {
        if (controller == tab) {
            tracker.trace(Events.markSubmit(myProject, controller.mySearchId, model.getHit().solutionId, null));
        }
    }
}
