package com.samebug.clients.idea.ui.controller.search;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.util.messages.MessageBusConnection;
import com.samebug.clients.idea.components.application.ClientService;
import com.samebug.clients.idea.components.application.IdeaSamebugPlugin;
import com.samebug.clients.idea.messages.view.MarkViewListener;
import com.samebug.clients.idea.messages.view.SearchGroupCardListener;
import com.samebug.clients.idea.messages.view.SearchTabsViewListener;
import com.samebug.clients.idea.ui.BrowserUtil;
import com.samebug.clients.idea.ui.component.organism.MarkPanel;
import com.samebug.clients.idea.ui.controller.TabController;
import com.samebug.clients.search.api.entities.RestHit;
import com.samebug.clients.search.api.entities.SearchGroup;
import com.samebug.clients.search.api.exceptions.SamebugClientException;
import org.jetbrains.annotations.NotNull;

public class ViewController
        implements SearchGroupCardListener, MarkViewListener, SearchTabsViewListener {
    final static Logger LOGGER = Logger.getInstance(ViewController.class);
    @NotNull
    final SearchTabController controller;

    public ViewController(@NotNull final SearchTabController controller) {
        this.controller = controller;

        MessageBusConnection projectMessageBus = controller.project.getMessageBus().connect(controller.project);
        projectMessageBus.subscribe(SearchGroupCardListener.TOPIC, this);
        projectMessageBus.subscribe(MarkViewListener.TOPIC, this);
        projectMessageBus.subscribe(SearchTabsViewListener.TOPIC, this);
    }

    @Override
    public void titleClick(@NotNull TabController tab, SearchGroup searchGroup) {
        if (controller == tab) {
            ApplicationManager.getApplication().assertIsDispatchThread();
            BrowserUtil.browse(IdeaSamebugPlugin.getInstance().getUrlBuilder().search(searchGroup.getLastSearch().id));
        }
    }

    @Override
    public void mark(MarkPanel.Model model) {
        if (model.getSearchId() == controller.mySearchId) {
            ApplicationManager.getApplication().assertIsDispatchThread();
            final ClientService client = IdeaSamebugPlugin.getInstance().getClient();
            final RestHit hit = model.getHit();
            try {
                if (hit.markId == null) client.postMark(model.getSearchId(), hit.solutionId);
                else client.retractMark(hit.markId);
            } catch (SamebugClientException e) {
                LOGGER.warn("Failed to execute mark.", e);
            }
        }
    }

    @Override
    public void reloadActiveSearchTab(@NotNull TabController tab) {
        if (this == tab) {
            ApplicationManager.getApplication().assertIsDispatchThread();
            controller.reload();
        }
    }

}
